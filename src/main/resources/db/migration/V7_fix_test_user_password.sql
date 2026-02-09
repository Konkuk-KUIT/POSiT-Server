UPDATE users
SET password = '$2a$10$T0kcl6QQcMc5oDMdOYvIRecSCH179zPaLY57Y/uEhQoOy93U8qyRO'
WHERE login_id IN (
                   'test_owner',
                   'owner1',
                   'owner2',
                   'owner3',
                   'owner4',
                   'owner5',
                   'owner6'
    );