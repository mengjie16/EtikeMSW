

INSERT INTO `supplier`
            (
             `name`,
             `company`,
             `industry`,
             `phone`,
             `create_time`,
             `update_time`)
VALUES (
        '徐总',       
        '上海童臻',  
        '玩具',
        '13817074887',
        NOW(),
        NOW());
        
INSERT INTO `user`
            (
             `name`,
             `phone`,
             `password`,
             `salt`,
             `role`,
             `user_id`,
             `create_time`,
             `last_login_time`)
VALUES (
        'tsbb_13817074887',  
        '13817074887',
        'd424df3de5fcddfe297bf9a33fc480b5',
        '412127',
        'SUPPLIER',
        '2005',
        NOW(),
NOW());