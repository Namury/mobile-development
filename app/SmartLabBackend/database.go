package main

import (
	"fmt"
	"log"
	"os"

	"database/sql"

	"github.com/joho/godotenv"

	_ "github.com/lib/pq" // PostgreSQL driver
)

var db *sql.DB

func InitDBPostgres() {
	err := godotenv.Load()
	if err != nil {
		log.Fatal("Error loading .env file")
	}

	dbHost := os.Getenv("DB_PG_HOST")
	dbPort := os.Getenv("DB_PG_PORT")
	dbUser := os.Getenv("DB_PG_USER")
	dbPass := os.Getenv("DB_PG_PASSWORD")
	dbName := os.Getenv("DB_PG_NAME")

	db, err = sql.Open("postgres", fmt.Sprintf("host=%s user=%s password=%s dbname=%s port=%s sslmode=disable", dbHost, dbUser, dbPass, dbName, dbPort))

	if err != nil {
		panic(err.Error())
	}

	err = db.Ping()
	if err != nil {
		panic(err.Error())
	}

	fmt.Println("Successfully connected to database")
}

func GetDB() *sql.DB {
	return db
}
