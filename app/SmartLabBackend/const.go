package main

const (
	getUserByDeviceSQL = `
		SELECT 
			id, 
			device_imei, 
			device_id, 
			checkin_status 
		FROM users 
		WHERE device_imei = $1 AND device_id = $2
	`

	createSensorsHistorySQL = `
		INSERT INTO sensors_history (
			user_id, 
			is_moving,
			direction, 
			ambient_light, 
			ambient_noise
		) VALUES ($1, $2, $3, $4, $5)
	`

	getDeviceListSQL = `
		SELECT device_name, is_occupied FROM devices
		ORDER BY device_name
	`

	getSummarySQL = `
		select 
			count(user_id) as total_checkins, 
			sum(case when is_moving then 1 else 0 end) as total_moving,
			avg(ambient_light) as average_ambient_light, 
			avg(ambient_noise) as average_ambient_noise
		from (
			select distinct on (user_id) user_id, ambient_light, ambient_noise, is_moving
			from sensors_history sh 
			where created_at >= NOW() - interval '%s minutes'
			order by user_id, created_at desc
		)
	`

	upsertCheckinSQL = `
		INSERT INTO users (device_id, device_imei, checkin_status)
		VALUES ($1, $2, true)
		ON CONFLICT (device_id, device_imei) 
		DO update SET 
			checkin_status = not users.checkin_status
		RETURNING checkin_status
	`

	upsertCheckinDeviceSQL = `
		UPDATE devices
		SET user_id=$1, is_occupied=not is_occupied, updated_at=now() 
		WHERE id=$2
		RETURNING device_name, is_occupied
	`
)
