CREATE TABLE IF NOT EXISTS "lesson" (
	"id" serial NOT NULL,
	"start_time" TIMESTAMPTZ NOT NULL,
	"end_time" TIMESTAMPTZ NOT NULL,
	"id_discipline" integer NOT NULL,
	"id_teacher" integer NOT NULL,
	"id_office" integer NOT NULL,
	"id_group" integer NOT NULL,
	CONSTRAINT "lesson_pk" PRIMARY KEY ("id")
) WITH (
  OIDS=FALSE
);


CREATE TABLE IF NOT EXISTS "discipline" (
	"id" serial NOT NULL,
	"name" TEXT NOT NULL,
	"short_name" TEXT NOT NULL,
	CONSTRAINT "discipline_pk" PRIMARY KEY ("id")
) WITH (
  OIDS=FALSE
);


CREATE TABLE IF NOT EXISTS "teacher" (
	"id" serial NOT NULL,
	"name" TEXT NOT NULL,
	"surname" TEXT NOT NULL,
	"patronymic" TEXT NOT NULL,
	"phone_number" TEXT NOT NULL,
	"description" TEXT NOT NULL,
	CONSTRAINT "teacher_pk" PRIMARY KEY ("id")
) WITH (
  OIDS=FALSE
);


CREATE TABLE IF NOT EXISTS "office" (
	"id" serial NOT NULL,
	"office" TEXT NOT NULL,
	CONSTRAINT "office_pk" PRIMARY KEY ("id")
) WITH (
  OIDS=FALSE
);


CREATE TABLE IF NOT EXISTS "group" (
	"id" serial NOT NULL,
	"name" TEXT NOT NULL,
	CONSTRAINT "group_pk" PRIMARY KEY ("id")
) WITH (
  OIDS=FALSE
);


CREATE TABLE IF NOT EXISTS "admin_panel_user" (
	"username" TEXT NOT NULL,
	"hashed_password" TEXT NOT NULL,
	"roles" TEXT[] NOT NULL,
	CONSTRAINT "user_pk" PRIMARY KEY ("username")
) WITH (
  OIDS=FALSE
);



ALTER TABLE "lesson" DROP CONSTRAINT IF EXISTS "lesson_fk0";
ALTER TABLE "lesson" DROP CONSTRAINT IF EXISTS "lesson_fk1";
ALTER TABLE "lesson" DROP CONSTRAINT IF EXISTS "lesson_fk2";
ALTER TABLE "lesson" DROP CONSTRAINT IF EXISTS "lesson_fk3";
ALTER TABLE "lesson" ADD CONSTRAINT "lesson_fk0" FOREIGN KEY ("id_discipline") REFERENCES "discipline"("id");
ALTER TABLE "lesson" ADD CONSTRAINT "lesson_fk1" FOREIGN KEY ("id_teacher") REFERENCES "teacher"("id");
ALTER TABLE "lesson" ADD CONSTRAINT "lesson_fk2" FOREIGN KEY ("id_office") REFERENCES "office"("id");
ALTER TABLE "lesson" ADD CONSTRAINT "lesson_fk3" FOREIGN KEY ("id_group") REFERENCES "group"("id");

INSERT INTO admin_panel_user (username, hashed_password, roles)
SELECT 'admin', '8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918', ARRAY['ADMIN']
WHERE NOT EXISTS (SELECT * FROM admin_panel_user);