	APIWebsiteCSVParser


		Pentru assignment-ul atasat, legat de API-ul care returneaza date despre o companie, am ales ca mod de 
	rezolvare Spring Boot Java, reusind sa creez un API care colecteaza, indexeaza si cauta informatii 
 	despre companiile din CSV.

		Primul lucru pe care l-am facut a fost sa analizez fisierele CSV pentru a-mi face o idee legata de 
 	cum voi parsa datele respective si legata de structura intregii	aplicatii. Am inceput prin a crea 
 	entitatea "Company" care avea ca field-uri headerele CSV-ului. Din acest punct am creat un controller 
 	(CompanyController)  responsabil pentru accesarea endpoint-ului care se ocupa de procesul de parsare a 
 	website-urilor din cadrul primei parti a assignment-ului si de asemenea, de procesul de cautare a 
  	field-urilor dorite.

		Pentru a accesa toate site-urile din cadrul CSV-ului, am decis sa le trimit in body-ul endpoint-ului 
 	ca o lista de strings, fiecare element fiind reprezentat de un site. Am decis ca cel mai rapid mod de 
  	a cauta prin sutele de site-uri a fost sa folosesc o cautare de tip async, prin care, cu ajutorul 
   	dependintei JSoup, ma conectez prin link-ul aferent la website si extrag informatiile necesare. 
    
    	Pentru a realiza acest lucru, a trebuit sa adaug o verificare pentru fiecare website in parte 
	si sa adaug la partea de conectare a JSoup prefixul https:// pentru a se efectua conectarea cu 
 	succes la website. Am folosit 2 functii, una prin care am verificat URL-ul pentru existenta unui 
  	prefix de tip https://, adaugandu-l daca este necesar si o alta functie prin care verificam daca 
   	host-ul era accesibil. La finalul acestei parti, pentru o usoara manipulare a datelor, 
 	am decis sa salvez toate companiile valide intr-un repository pentru a le folosi in cadrul 
  	urmatoarelor parti ale assignment-ului.

		Pentru partea a doua a assignment-ului, legata de Data Retrieval, am incarcat fisierul CSV oferit 
 	in resursele proiectului, urmand sa parsez informatiile intr-un obiect care contine ca field-uri 
  	headerele fisierului CSV. Pentru parsare, am folosit dependinta CsvToBean pentru a extrage datele 
   	necesare, pe care le-am inserat intr-o lista. Ulterior, am apelat functia findAll() 
 	din cadrul repository-ului in care am salvat datele din prima parte, le-am introdus si pe acestea 
  	intr-o lista, iar ambele liste le-am trimis ca parametru intr-un service care se ocupa de 
   	imbinarea acestor date. Am creat un obiect care contine field-urile obiectelor din primul CSV 
    combinate cu field-urile obiectelor din al doilea CSV. Am salvat aceste date imbinate 
 	intr-o alta entitate, de tip CompanyMerged. Cu toate acestea am creat o lista pe care am salvat-o 
  	la randul ei in repository-ul aferent CompanyMerged, pentru a le folosi ulterior la nevoie. 
   	In ultima parte a acestui service, pe langa salvarea in repository, am indexat datele in 
    Elasticsearch pentru a-mi fi de folos in ultima parte a assignment-ului, convertind 
     informatiile intr-un json.

		La ultima parte a assignment-ului am stabilit criteriile de cautare pentru fiecare parametru dat, 
 	in cadrul Elasticsearch, iar prin folosirea RestHighLevelClient si a functiei de search am cautat 
  	datele cerute in cele salvate anterior in Elasticsearch. Rezultatele au fost returnate sub forma 
   	unui SearchHit[] pe care m-am asigurat ca il convertesc intr-un obiect de tip CompanyMerged 
 	pentru o afisare relevanta. De asemenea, am implementat si o metoda prin care calculez "scorul" 
  	fiecarui match facut. Am luat fiecare parametru in parte si am calculat un maxim al "scorului" 
   	pe care acesta il acumuleaza in urma compararii fiercarui field din request cu field-urile 
    obiectelor procesate, determinandu-se astfel cel mai bun match.

		O parte interesanta a acestui assignment a fost determinata de implementarea Elasticsearch in 
 	cadrul proiectului. Pentru acest lucru, am deschis serverul asociat Elasticsearch, am creat o 
  	clasa de config pentru crearea unei instante de RestHighLevelClient de 
	tip @Bean pentru operatiunile din cadrul Elasticsearch. Pentru initializare, am creat o clasa 
 	de initializare in care am stabilit setarile predefinite pentru cautare si crearea indexului 
  	folosit de Elasticsearch. De asemenea, am creat un controller aferent pentru a apela crearea 
   	indexului inainte de a incepe sa verific celelalte endpoint-uri.
