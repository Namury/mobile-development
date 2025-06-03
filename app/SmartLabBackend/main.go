package main

import (
	"bytes"
	"encoding/json"
	"fmt"
	"net/http"
	"strings"

	"log"

	"github.com/gin-gonic/gin"
	"github.com/joho/godotenv"
)

func main() {
	// initiate env
	err := godotenv.Load(".env")
	if err != nil {
		log.Fatal("Error loading .env file")
	}

	InitDBPostgres()

	router := gin.Default()
	router.GET("/summary", getSummary)
	router.POST("/checkin", checkin)
	router.POST("/store-sensors", storeSensors)

	router.Run(":8080")
}

func storeSensors(c *gin.Context) {
	var sensorRequest storeSensorsRequest

	if c.ContentType() != "application/x-www-form-urlencoded" {
		raw, err := c.GetRawData()
		if err != nil {
			log.Printf("Error Raw Data: %v", err)
			return
		}
		var buf bytes.Buffer
		if err := json.Compact(&buf, raw); err != nil {
			log.Printf("Error Compact: %v", err)
			return
		}
		data := buf.Bytes()
		err = json.Unmarshal(data, &sensorRequest); if err != nil {
			log.Printf("Error Unmarshal: %v", err)
			c.JSON(http.StatusBadRequest, gin.H{"status": "error", "message": "Invalid request data"})
			return
		}
	} else {
		// Call BindJSON to bind the received JSON
		if err := c.ShouldBind(&sensorRequest); err != nil {
			log.Printf("Error binding JSON: %v", err)
			c.JSON(http.StatusBadRequest, gin.H{"status": "error", "message": "Invalid request data"})
			return
		}
	}

	db := GetDB()

	// get user by device id and imei
	var userDetail user
	err := db.QueryRow(getUserByDeviceSQL,
		sensorRequest.DeviceImei,
		sensorRequest.DeviceID).Scan(&userDetail.Id, &userDetail.DeviceImei, &userDetail.DeviceId, &userDetail.CheckinStatus)
	if err != nil {
		log.Printf("Error retrieving user ID: %v", err)
		c.JSON(http.StatusInternalServerError, gin.H{"status": "error", "message": "Failed to retrieve user ID"})
		return
	}

	// Insert the sensor data into the database
	_, err = db.Exec(createSensorsHistorySQL,
		userDetail.Id,
		sensorRequest.IsMoving,
		sensorRequest.Direction,
		sensorRequest.AmbientLight,
		sensorRequest.AmbientNoise,
	)
	if err != nil {
		log.Printf("Error inserting sensor data: %v", err)
		c.JSON(http.StatusInternalServerError, gin.H{"status": "error", "message": "Failed to store sensor data"})
		return
	}

	sensorResponse := storeSensorsResponse{
		Status:  "success",
		Message: "Sensor data stored successfully",
	}

	c.JSON(http.StatusCreated, sensorResponse)
}

func checkin(c *gin.Context) {
	var checkinRequest checkinRequest
	var response checkinResponse

	if c.ContentType() != "application/x-www-form-urlencoded" {
		raw, err := c.GetRawData()
		if err != nil {
			log.Printf("Error Raw Data: %v", err)
			return
		}
		var buf bytes.Buffer
		if err := json.Compact(&buf, raw); err != nil {
			log.Printf("Error Compact: %v", err)
			return
		}
		data := buf.Bytes()
		err = json.Unmarshal(data, &checkinRequest); if err != nil {
			log.Printf("Error Unmarshal: %v", err)
			c.JSON(http.StatusBadRequest, gin.H{"status": "error", "message": "Invalid request data"})
			return
		}
	} else {
		// Call BindJSON to bind the received JSON
		if err := c.ShouldBind(&checkinRequest); err != nil {
			log.Printf("Error binding JSON: %v", err)
			c.JSON(http.StatusBadRequest, gin.H{"status": "error", "message": "Invalid request data"})
			return
		}

	}

	db := GetDB()
	// Insert the check-in data into the database

	if strings.Contains(checkinRequest.CheckinID, "checkin_device") {
		deviceId := strings.TrimPrefix(checkinRequest.CheckinID, "checkin_device_")

		// get user by device id and imei
		var userDetail user
		err := db.QueryRow(getUserByDeviceSQL,
			checkinRequest.DeviceImei,
			checkinRequest.DeviceID).Scan(&userDetail.Id, &userDetail.DeviceImei, &userDetail.DeviceId, &userDetail.CheckinStatus)
		if err != nil {
			log.Printf("Error retrieving user ID: %v", err)
			c.JSON(http.StatusInternalServerError, gin.H{"status": "error", "message": "Failed to retrieve user ID"})
			return
		}

		// Insert the check-in data into the database
		var deviceName string
		var isOccupied bool
		err = db.QueryRow(upsertCheckinDeviceSQL,
			userDetail.Id,
			deviceId).Scan(&deviceName ,&isOccupied)
		if err != nil {
			log.Printf("Error check-in device: %v", err)
			response = checkinResponse{
				Status:  "failed",
				Message: "Failed to check-in device",
			}
			c.JSON(http.StatusInternalServerError, response)
			return
		}
		
		if isOccupied {
			response = checkinResponse{
				Status:  "success",
				Message: fmt.Sprintf("Device %s checked in successfully", deviceName),
			}
			c.JSON(http.StatusCreated, response)
			return
		} else {
			response = checkinResponse{
				Status:  "success",
				Message: fmt.Sprintf("Device %s checked out successfully", deviceName),
			}
			c.JSON(http.StatusOK, response)
			return
		}

	} else {
		var checkinStatus bool
		err := db.QueryRow(upsertCheckinSQL,
			checkinRequest.DeviceImei,
			checkinRequest.DeviceID,
		).Scan(&checkinStatus)
		if err != nil {
			log.Printf("Error check-in lab: %v", err)
			response = checkinResponse{
				Status:  "failed",
				Message: "Failed to check-in lab",
			}
			c.JSON(http.StatusInternalServerError, response)
			return
		}

		if checkinStatus {
			response = checkinResponse{
				Status:  "success",
				Message: "Check-in successful",
			}
			c.JSON(http.StatusCreated, response)
			return
		} else {
			response = checkinResponse{
				Status:  "success",
				Message: "Check-out successful",
			}
			c.JSON(http.StatusOK, response)
			return
		}
	}
}

func getSummary(c *gin.Context) {
	interval := c.Query("interval")
	deviceImei := c.Query("device_imei")
	deviceId := c.Query("device_id")

	db := GetDB()

	// get user by device id and imei
	var userDetail user
	err := db.QueryRow(getUserByDeviceSQL,
		deviceImei,
		deviceId).Scan(&userDetail.Id, &userDetail.DeviceImei, &userDetail.DeviceId, &userDetail.CheckinStatus)
	if err != nil {
		log.Printf("Error retrieving user ID: %v", err)
		c.JSON(http.StatusInternalServerError, gin.H{"status": "error", "message": "Failed to retrieve user ID"})
		return
	}

	// Query the database for summary data
	var summary summary
	err = db.QueryRow(fmt.Sprintf(getSummarySQL, interval)).Scan(
		&summary.TotalCheckins,
		&summary.TotalMoving,
		&summary.AverageAmbientLight,
		&summary.AverageAmbientNoise,
	)
	if err != nil {
		log.Printf("Error querying summary data: %v", err)
		c.JSON(http.StatusInternalServerError, gin.H{"status": "error", "message": "Failed to retrieve summary data"})
		return
	}

	summary.CheckinStatus = userDetail.CheckinStatus

	// get device list
	rows, err := db.Query(getDeviceListSQL)
	if err != nil {
		log.Printf("Error retrieving device list: %v", err)
		c.JSON(http.StatusInternalServerError, gin.H{"status": "error", "message": "Failed to retrieve device list"})
		return
	}
	defer rows.Close()
	var devicesList []devices
	for rows.Next() {
		var device devices
		if err := rows.Scan(&device.DeviceName, &device.IsOccupied); err != nil {
			log.Printf("Error scanning device row: %v", err)
			c.JSON(http.StatusInternalServerError, gin.H{"status": "error", "message": "Failed to scan device row"})
			return
		}
		devicesList = append(devicesList, device)
	}
	summary.Devices = devicesList

	summaryResponse := summaryResponse{
		Status:  "success",
		Message: "Summary data retrieved successfully",
		Data:    summary,
	}

	c.JSON(http.StatusOK, summaryResponse)
}
