USE shanwei_skill;

INSERT INTO `user` (username, password, nickname, email, phone, avatar, create_time)
SELECT 'admin', 'admin123', '管理员', 'admin@swu.edu.cn', '13800000000',
       'https://api.dicebear.com/8.x/initials/svg?seed=admin', NOW()
WHERE NOT EXISTS (
  SELECT 1 FROM `user` WHERE username = 'admin'
);
