USE shanwei_skill;

INSERT INTO `user` (id, username, password, nickname, email, phone, avatar, create_time) VALUES
(1, 'studentA', '123456', '张三', 'a@swu.edu.cn', '13800000001', 'https://api.dicebear.com/8.x/initials/svg?seed=A', NOW()),
(2, 'studentB', '123456', '李四', 'b@swu.edu.cn', '13800000002', 'https://api.dicebear.com/8.x/initials/svg?seed=B', NOW()),
(3, 'admin', 'admin123', '管理员', 'admin@swu.edu.cn', '13800000000', 'https://api.dicebear.com/8.x/initials/svg?seed=admin', NOW());

INSERT INTO skill_category (name, description) VALUES
('编程开发', 'Python、Web、Java、数据分析等'),
('设计创作', 'PS、海报、PPT、美工设计等'),
('语言学习', '英语、日语、口语陪练等'),
('摄影剪辑', '拍摄、修图、短视频剪辑等'),
('课程辅导', '数学、物理、专业课辅导等');

INSERT INTO skill (skill_id, user_id, skill_name, category, description, price_type, service_mode, price, skill_level, status, create_time) VALUES
(1, 1, 'Python数据分析', '编程开发', '提供 Pandas、Matplotlib、课程项目和论文数据处理辅导。', '校园互助/面议', '线上互助', '30元/次', '熟练', 'ONLINE', NOW()),
(2, 1, 'Python课程辅导', '编程开发', '帮助零基础同学理解语法、作业和小项目。', '免费互助', '线上互助', '免费', '熟练', 'ONLINE', NOW()),
(3, 2, '毕业答辩PPT美化', '设计创作', '优化答辩PPT版式、配色和逻辑结构。', '50元以内/面议', '校园线下互助', '50元以内', '熟练', 'ONLINE', NOW()),
(4, 2, '校园摄影约拍', '摄影剪辑', '提供证件照、活动照、人像拍摄与基础修图。', '互助/面议', '校园线下互助', '面议', '进阶', 'ONLINE', NOW());

INSERT INTO service_request (user_id, title, description, category, budget, deadline, status, create_time) VALUES
(2, '需要制作毕业海报', '希望帮忙设计毕业季活动海报，风格清爽。', '设计创作', '50元', '2026-12-01 00:00:00', 'OPEN', NOW());
