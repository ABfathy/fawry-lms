-- Promote the registered local bootstrap candidate to ADMIN.
-- Supply the exact adminEmail saved by the Postman bootstrap request as admin_email.
UPDATE users
SET role = 'ADMIN'
WHERE email = :'admin_email'
RETURNING id, email, role;
