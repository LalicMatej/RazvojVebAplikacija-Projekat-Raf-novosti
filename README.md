# RAF News

RAF News je web aplikacija za prikaz, pretragu i administraciju vesti. Projekat je podeljen na backend REST API pisan u Javi/Jersey tehnologiji i frontend klijent pisan u React + TypeScript okruženju.

## Funkcionalnosti

- pregled najnovijih, najčitanijih i najviše reagovanih vesti
- filtriranje vesti po kategoriji i tagu
- detaljan prikaz vesti sa povezanim vestima
- komentari i reakcije na vesti/komentare
- prijava korisnika preko JWT tokena
- CMS deo za upravljanje vestima i kategorijama
- administratorski deo za upravljanje korisnicima

## Tehnologije

### Backend

- Java
- Jersey REST API
- Maven
- MySQL
- JWT autentifikacija
- Jackson JSON serijalizacija

### Frontend

- React
- TypeScript
- Vite
- React Router
- React Bootstrap
- Axios

## Struktura projekta

```text
.
├── backendprojekat/      # Java/Jersey backend aplikacija
│   ├── pom.xml
│   └── src/main/java/org/raflab/backendprojekat/
│       ├── model/        # domenski modeli
│       ├── repository/   # MySQL repozitorijumi
│       ├── resources/    # REST endpointi
│       ├── service/      # poslovna logika
│       ├── filters/      # CORS i auth filteri
│       └── dtos/         # request/response DTO klase
└── frontend/             # React frontend aplikacija
    ├── package.json
    └── src/
        ├── api/          # Axios API klijenti
        ├── components/   # reusable komponente
        ├── pages/        # stranice aplikacije
        └── types/        # TypeScript tipovi
```

## Preduslovi

Pre pokretanja instalirati:

- Java JDK
- Maven ili koristiti priloženi Maven wrapper
- MySQL server
- Node.js i npm
- servlet container, npr. Apache Tomcat, za pokretanje `.war` backend aplikacije

## Podešavanje baze

Backend trenutno očekuje lokalnu MySQL bazu sa sledećim parametrima:

```text
host: localhost
port: 3306
database: raf_news
username: root
password: root
```

Podešavanje konekcije nalazi se u:

```text
backendprojekat/src/main/java/org/raflab/backendprojekat/repository/MySqlAbstractRepository.java
```

Ako su kredencijali drugačiji na lokalnoj mašini, promeniti vrednosti metoda `getDatabaseName`, `getUsername` i `getPassword`.

## Pokretanje backend-a

Iz root direktorijuma projekta:

```bash
cd backendprojekat
./mvnw clean package
```

Na Windows-u:

```bash
cd backendprojekat
mvnw.cmd clean package
```

Nakon build-a, deploy-ovati generisani `.war` fajl iz `target` direktorijuma na Tomcat. Frontend očekuje backend na:

```text
http://localhost:8080/raf-news
```

REST API je registrovan pod Jersey application path-om:

```text
/api
```

Primer pune putanje endpointa:

```text
http://localhost:8080/raf-news/api/news/latest
```

## Pokretanje frontend-a

```bash
cd frontend
npm install
npm run dev
```

Vite development server se podrazumevano pokreće na:

```text
http://localhost:5173
```

Backend CORS filter trenutno dozvoljava zahteve sa `http://localhost:5173`.

## Frontend skripte

U direktorijumu `frontend` dostupne su sledeće komande:

```bash
npm run dev      # pokretanje development servera
npm run build    # TypeScript provera i produkcioni build
npm run lint     # ESLint provera
npm run preview  # preview produkcionog build-a
```

## Glavne rute u aplikaciji

- `/` - početna strana
- `/login` - prijava korisnika
- `/most-read` - najčitanije vesti
- `/news` - lista vesti
- `/news/category/:categoryId` - vesti po kategoriji
- `/news/tag/:tagId` - vesti po tagu
- `/news/:id` - detalj vesti
- `/cms` - CMS kategorije
- `/cms/news` - CMS vesti
- `/cms/users` - administracija korisnika

## Važniji API endpointi

Osnovna API putanja:

```text
http://localhost:8080/raf-news/api
```

Primeri endpointa:

- `POST /auth/login` - prijava korisnika
- `POST /auth/logout` - odjava korisnika
- `GET /news/latest` - najnovije vesti
- `GET /news/most-read` - najčitanije vesti
- `GET /news/most-reacted` - vesti sa najviše reakcija
- `GET /news/search` - pretraga vesti
- `GET /news/by-category/{categoryId}` - vesti po kategoriji
- `GET /news/by-tag/{tagId}` - vesti po tagu
- `GET /news/{id}` - detalj vesti
- `POST /news` - kreiranje vesti
- `PUT /news/{id}` - izmena vesti
- `DELETE /news/{id}` - brisanje vesti
- `GET /categories` - lista kategorija
- `POST /categories` - kreiranje kategorije
- `PUT /categories/{id}` - izmena kategorije
- `DELETE /categories/{id}` - brisanje kategorije
- `GET /tags` - lista tagova
- `GET /users` - lista korisnika
- `POST /users` - kreiranje korisnika
- `PUT /users/{id}` - izmena korisnika
- `PUT /users/{id}/status` - promena statusa korisnika
- `GET /news/{newsId}/comments` - komentari za vest
- `POST /news/{newsId}/comments` - dodavanje komentara
- `POST /news/{newsId}/reactions` - reakcija na vest

## Autentifikacija i role

Prijava se vrši preko `POST /auth/login`. Backend vraća JWT token, a frontend ga čuva u `localStorage` pod ključem `jwt` i šalje kroz `Authorization: Bearer <token>` header.

Postoje dve role korisnika:

- `CONTENT_CREATOR` - pristup CMS funkcionalnostima za sadržaj
- `ADMIN` - dodatni pristup administraciji korisnika

## Build za produkciju

Frontend:

```bash
cd frontend
npm run build
```

Backend:

```bash
cd backendprojekat
./mvnw clean package
```

Produkcioni frontend build nalazi se u `frontend/dist`, a backend `.war` fajl u `backendprojekat/target`.
"# RazvojVebAplikacija-Projekat-Raf-novosti" 
