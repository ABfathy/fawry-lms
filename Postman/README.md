# Fawry LMS Postman collections

## Import into Postman

The two `.postman_collection.json` files use Postman Collection v2.1. Import them in Postman with **Import**. Import `Fawry LMS Local.postman_environment.json` too, then select the **Fawry LMS Local** environment.

## Start the API

From the repository root, start PostgreSQL and the app:

```sh
docker compose up -d db
./mvnw spring-boot:run
```

Spring reads the root `.env` when the app starts. The project requires Java 25. Postman uses `http://localhost:8080` by default. Change `baseUrl` in the environment if your app listens elsewhere.

## Create the local admin account

The API registration route creates student accounts only. Send **Health check** and then **Register admin candidate** in the bootstrap collection. Copy the generated `adminEmail` value from the selected Postman environment.

Promote that account in the local database. Replace the sample email with the exact adminEmail value from Postman, then run this as one line from the repository root. The command uses psql, the PostgreSQL client inside the db container:

```sh
docker compose exec -T db psql -U postgres -d fawry_lms -v admin_email='postman-admin-...@example.test' -f - < Postman/promote-admin.sql
```

Success prints the account with role `ADMIN` and ends with `UPDATE 1`.

Now send **Login admin** in the API collection. It saves the admin JWT as `adminToken`. You only need to promote an account once for each local database.

## Run the API requests

Send these requests in order. The collection saves IDs and tokens as later requests need them.

1. In **01 - Authentication**, send **Login admin**, **Register student**, and **Login student**.
2. In **02 - Student profiles**, send **List students (capture registered profile ID)**. It finds the new student by email and saves the database ID as `studentId`. The remaining requests in this folder exercise student CRUD with a temporary record.
3. Send **03 - Instructor CRUD**, then **04 - Course CRUD and cleanup**. The course is deleted before its instructor.
4. Send **05 - Instructor-owned course**. It creates and signs in as an instructor, then creates a course owned by that instructor.
5. In **06 - Enrollment and payment**, create an enrollment, send the signed PAID webhook, inspect the result, then cancel the enrollment. The webhook script signs the request with `PAYMENT_WEBHOOK_SECRET` from the local Postman environment.

The CRUD fixtures are deleted by their cleanup requests. The admin account, student account, enrollment instructor, course, and canceled enrollment remain in the local database. Each run generates new fixture emails and course codes.

## Keep local secrets local

The root `.env` and `Fawry LMS Local.postman_environment.json` are ignored by Git. The Postman environment contains the webhook secret and captures access tokens after login. The `.example.postman_environment.json` file is a template with no webhook secret. Do not commit or share the local files. Use new secrets outside local development.
