CREATE TABLE patent (
    patnum 	char(7) not null unique,
	idate 	varchar(20),
	title 	varchar(400),
	inventors varchar(600),
	assignee varchar(150),
	familyID varchar(15),
	applNum  varchar(10),
	dateFiled varchar(20),
	docID	  varchar(17),
	pubDate   varchar(12),
	USclass   varchar(3),
	examiner  varchar(30),
	legalfirm varchar(70)
);