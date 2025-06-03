package main

type summaryResponse struct {
	Status  string  `json:"status"`
	Message string  `json:"message"`
	Data    summary `json:"data"`
}

type user struct {
	Id            string `json:"id" db:"id"`
	DeviceId      string `json:"device_id" db:"device_id"`
	DeviceImei    string `json:"device_imei" db:"device_imei"`
	CheckinStatus bool   `json:"checkin_status" db:"checkin_status"`
}

type summary struct {
	CheckinStatus       bool      `json:"checkin_status" db:"checkin_status"`
	TotalCheckins       int       `json:"total_checkins" db:"total_checkins"`
	TotalMoving         int       `json:"total_moving" db:"total_moving"`
	AverageAmbientLight float32   `json:"average_ambient_light" db:"average_ambient_light"`
	AverageAmbientNoise float32   `json:"average_ambient_noise" db:"average_ambient_noise"`
	Devices             []devices `json:"devices"`
}

type devices struct {
	DeviceName string `json:"device_name" db:"device_name"`
	IsOccupied bool   `json:"is_occupied" db:"is_occupied"`
}

type storeSensorsRequest struct {
	DeviceImei   string `form:"device_imei" json:"device_imei" db:"device_imei"`
	DeviceID     string `form:"device_id" json:"device_id" db:"device_id"`
	IsMoving     bool   `form:"is_moving" json:"is_moving" db:"is_moving"`
	Direction    string `form:"direction" json:"direction" db:"direction"`
	AmbientLight int    `form:"ambient_light" json:"ambient_light" db:"ambient_light"`
	AmbientNoise int    `form:"ambient_noise" json:"ambient_noise" db:"ambient_noise"`
	Timestamp    string `form:"timestamp" json:"timestamp" db:"timestamp"`
}

type storeSensorsResponse struct {
	Status  string `json:"status"`
	Message string `json:"message"`
}

type checkinRequest struct {
	DeviceImei string `form:"device_imei" json:"device_imei" db:"device_imei"`
	DeviceID   string `form:"device_id" json:"device_id" db:"device_id"`
	CheckinID  string `form:"checkin_id" json:"checkin_id" db:"checkin_id"`
}

type checkinResponse struct {
	Status  string `json:"status"`
	Message string `json:"message"`
}
