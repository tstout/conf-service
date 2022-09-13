MERGE INTO conf.account
--(account_id, name, description, user, pass)
KEY(account_id)
VALUES (?, ?, ?, ?, ?)
