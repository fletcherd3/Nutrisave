# Nutrisave by the Navbar Pigeons

Web Application for the Nutrisave project by Navbara Pigeons using `gradle`, `npm`, `Spring Boot`
, `Vue.js`, `Gitlab CI` and more!  
Refer to
our [wiki for more information](https://eng-git.canterbury.ac.nz/seng302-2021/team-300/-/wikis/home)
.

## Abstract

The number of people on specific diets is on the rise and while they are good for your health,
experience will show they can be harmful to your wallet. The Navbara Pigeons have spent this year
developing the solution to cater to the growing needs for nutritious food that doesn’t break the
bank.

Nutrisave is a sales platform for near-expiring goods with a focus on providing the user with
health-related information on the products. Users can view the nutritional and processed rating as
well as the fat, sugar, and salt content of the food they buy. Users also have the ability to search
products by their diet: No Gluten? No problem. Prefer veggies over meat? Easy!

Nutrisave also makes the tedious task of adding products into the app an ease. Business owners can
simply scan the barcode of the product or enter its number to autofill product and nutritional
information.

## Project Structure

### Frontend

#### Directories

A frontend sub-project (web GUI):

- `frontend/src` Frontend source code (Vue.js)
- `frontend/public` publicly accessible web assets (e.g., icons, images, style sheets)
- `frontend/dist` Frontend production build

### Backend

#### Directories

A backend sub-project (business logic and persistence server):

- `backend/src` Backend source code (Java - Spring)
- `backend/build` Backend production build

#### Specification

You can visualise and interact with the API specification by visiting the Swagger-UI web page
at https://csse-s302g3.canterbury.ac.nz/test/api/swagger-ui/index.html?url=/test/api/api-docs.yaml.

Alternatively, the OpenAPI YAML specification can be accessed
at https://csse-s302g3.canterbury.ac.nz/test/api/api-docs.yaml.

## Credentials

User with business, products, listings, etc.:

- Username: `dnb36@uclive.ac.nz`
- Password: `fun123`

Admin (DGAA):

- Username: `admin@wasteless.co.nz`
- Password: `admin`

Regular user:

- Username: `fdi19@uclive.ac.nz`
- Password: `fun123`

## How to run

### Frontend / GUI

    $ cd frontend
    $ npm install
    $ npm run serve

Running on: http://localhost:9500/ by default

### Backend / server

    cd backend
    ./gradlew bootRun

Running on: http://localhost:9499/ by default

You will need to have a database instance running somewhere  
Look [HERE](https://www.cosc.canterbury.ac.nz/policy/labs/remoteconnection.shtml) to see how to SSH
into the UC network

Add these environment variables to your run configurations:

- DB_USERNAME=abc123; <-- (Database username)
- DB_URL_TEST=jdbc:mariadb://localhost:3306/abc123_seng302; <-- (Database url)
- DB_PASSWORD=123456789 <-- (Database password)

### Adding dummy data

Wiki page on how to add the data
here: https://eng-git.canterbury.ac.nz/seng302-2021/team-300/-/wikis/Inserting%20test%20data

## Contributors

- Alec Fox (amf133)
- Fletcher Dick (fdi19)
- Haruka Ichinose (hic21)
- Jordan Pyott (jpy19)
- Maximilian Birzer (mbi47)
- Niko Tainui (nbt25)
- Rio Ogino (rog19)
- Dawson Berry (dnb36)

## References

- [Spring Boot Docs](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
- [Spring JPA docs](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [Vue docs](https://vuejs.org/v2/guide/)
- [Learn resources](https://learn.canterbury.ac.nz/course/view.php?id=10577&section=11)
