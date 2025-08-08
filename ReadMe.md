Mündəricat
1. Giriş: GraphQL Nədir? (#giriş-graphql-nədir)
2. Əsas Problem: GraphQL Nəyi Həll Edir? (REST ilə Müqayisə) (#əsas-problem-graphql-nəyi-həll-edir-rest-ilə-müqayisə)
3. GraphQL-in Üç Əsas Konsepti (#graphql-in-üç-əsas-konsepti)
    * Schema və Tiplər (The Schema & Types) (#schema-və-tiplər-the-schema--types)
    * Sorğular (Queries) (#sorğular-queries)
    * Mutasiyalar (Mutations) (#mutasiyalar-mutations)
4. Əlavə Konsept: Abunəliklər (Subscriptions) (#əlavə-konsept-abunəliklər-subscriptions)
5. Praktiki Nümunə: Sadə GraphQL Serverinin Qurulması (Node.js) (#praktiki-nümunə-sadə-graphql-serverinin-qurulması-nodejs)
6. GraphQL-in Əsas Xüsusiyyətləri və Üstünlükləri (#graphql-in-əsas-xüsusiyyətləri-və-üstünlükləri)
7. GraphQL vs. REST: Müqayisəli Cədvəl (#graphql-vs-rest-müqayisəli-cədvəl)
8. Nəticə və Növbəti Addımlar (#nəticə-və-növbəti-addımlar)

  ---

1. Giriş: GraphQL Nədir?

GraphQL — API-lar üçün sorğu dili (query language) və bu sorğuları icra etmək üçün bir mühitdir (runtime). 2012-ci ildə Facebook tərəfindən daxili istifadə üçün yaradılıb və 2015-ci ildə açıq mənbə olaraq təqdim
edilib.

Ən sadə izahla, GraphQL client-ə (məsələn, veb və ya mobil tətbiq) serverdən tam olaraq hansı məlumatlara ehtiyacı olduğunu bildirməyə imkan verir. Server isə cavab olaraq yalnız tələb olunan məlumatları JSON
formatında qaytarır.

> 💡 Açar Fikir: REST API-də server məlumatın strukturunu müəyyən edirsə, GraphQL-də client məlumatın strukturunu tələb edir.

  ---

2. Əsas Problem: GraphQL Nəyi Həll Edir? (REST ilə Müqayisə)

Tradisional REST API-lərdə iki əsas problem mövcuddur:

a) Over-fetching (Həddindən Artıq Məlumat Yükləmə)
Client-ə lazım olduğundan daha çox məlumatın göndərilməsi.

* REST Nümunəsi: İstifadəçinin sadəcə adını göstərmək üçün /users/1 endpoint-inə müraciət edirsiniz, amma server sizə istifadəçinin adı, doğum tarixi, ünvanı, qeydiyyat tarixi və s. kimi bütün məlumatları qaytarır.

b) Under-fetching (Əskik Məlumat Yükləmə)
Lazım olan bütün məlumatları əldə etmək üçün bir neçə fərqli endpoint-ə sorğu göndərmək məcburiyyəti.

* REST Nümunəsi: Bir bloq səhifəsində istifadəçinin adını, onun məqalələrini və hər məqalənin şərhlərini göstərmək üçün 3 fərqli sorğu göndərməlisiniz:
    1. /users/1 (istifadəçi məlumatları üçün)
    2. /users/1/posts (istifadəçinin məqalələri üçün)
    3. /posts/101/comments (bir məqalənin şərhləri üçün)

GraphQL-in Həlli:
GraphQL bu iki problemi tək bir sorğu ilə həll edir. Client lazım olan bütün məlumatları və onların strukturunu bir sorğuda təsvir edir və tək bir endpoint-ə (/graphql) göndərir.

* GraphQL Nümunəsi:
  1     query {
  2       user(id: "1") {
  3         name
  4         posts {
  5           title
  6           comments {
  7             body
  8           }
  9         }
  10       }
  11     }
  Bu sorğu nəticəsində server yalnız istifadəçinin adını, məqalələrinin başlığını və hər məqalənin şərh mətnini qaytaracaq. Nə artıq, nə də əskik.

  ---

3. GraphQL-in 3 Əsas Konsepti

a) Schema və Tiplər (The Schema & Types)

GraphQL API-nin mərkəzində Schema (sxem) dayanır. Schema, API-nin hansı məlumatları təqdim edə biləcəyini təsvir edən bir "müqavilədir". O, client-in hansı sorğuları göndərə biləcəyini müəyyən edir.

Schema tiplərdən ibarətdir.

* Scalar Tiplər: Ən sadə məlumat növləri.
    * Int: Tam ədəd
    * Float: Kəsr ədəd
    * String: Sətir
    * Boolean: true və ya false
    * ID: Unikal identifikator

* Obyekt Tipləri: Öz sahələri (fields) olan xüsusi tiplər.

Nümunə Schema (GraphQL Schema Definition Language - SDL ilə):

    1 # Bir istifadəçini təsvir edir
    2 type User {
    3   id: ID!
    4   name: String!
    5   email: String
    6   posts: [Post!]
    7 }
    8 
    9 # Bir məqaləni təsvir edir
10 type Post {
11   id: ID!
12   title: String!
13   body: String!
14   author: User!
15 }
16
17 # Client-in hansı məlumatları "oxuya" biləcəyini müəyyən edir
18 type Query {
19   users: [User!]
20   user(id: ID!): User
21   posts: [Post!]
22   post(id: ID!): Post
23 }
> 💡 Qeyd: ! işarəsi həmin sahənin "məcburi" (non-nullable) olduğunu, yəni null qaytara bilməyəcəyini bildirir. [Post!] isə Post-lardan ibarət bir massiv deməkdir.

b) Sorğular (Queries)

Query — serverdən məlumat oxumaq (fetch) üçün istifadə olunur. Bu, REST-dəki GET sorğusunun analoqudur. Client hansı sahələri istədiyini dəqiq şəkildə göstərir.

Nümunə 1: Bütün istifadəçilərin adını və emailini almaq
1 query {
2   users {
3     name
4     email
5   }
6 }

Nümunə 2: ID-si "1" olan istifadəçini və onun məqalələrinin başlığını almaq

1 query GetUserWithPosts {
2   user(id: "1") {
3     name
4     posts {
5       title
6     }
7   }
8 }

c) Mutasiyalar (Mutations)

Mutation — serverdə məlumatı dəyişdirmək (yaratmaq, yeniləmək, silmək) üçün istifadə olunur. Bu, REST-dəki POST, PUT, PATCH, DELETE sorğularının analoqudur.

Mutasiyalar da sorğular kimi sahələr qaytarır. Bu, bir əməliyyatdan sonra serverdəki yenilənmiş məlumatı dərhal geri almağa imkan verir.

Schema-ya Mutation əlavə edək:
1 type Mutation {
2   createUser(name: String!, email: String!): User!
3   updatePost(id: ID!, title: String!): Post
4   deleteUser(id: ID!): Boolean
5 }

Nümunə: Yeni istifadəçi yaratmaq və onun ID və adını geri almaq
1 mutation {
2   createUser(name: "Ali Valiyev", email: "ali@example.com") {
3     id
4     name
5   }
6 }

  ---

4. Əlavə Konsept: Abunəliklər (Subscriptions)

Subscription — serverlə real-zamanlı (real-time) əlaqə qurmaq üçün istifadə olunur. Client müəyyən bir hadisəyə "abunə olur" və həmin hadisə baş verdikdə server client-ə avtomatik olaraq məlumat göndərir.

Bu, adətən WebSockets üzərindən işləyir və canlı çat, bildirişlər, anlıq yenilənən məlumatlar üçün idealdır.

Schema-ya Subscription əlavə edək:
1 type Subscription {
2   newPost: Post!
3 }

Nümunə: Yeni məqalə yaradıldıqda anında məlumat almaq

1 subscription {
2   newPost {
3     id
4     title
5     author {
6       name
7     }
8   }
9 }

  ---

5. Praktiki Nümunə: Sadə GraphQL Serverinin Qurulması (Node.js)

Bunun üçün Apollo Server kitabxanasından istifadə edəcəyik.

Addım 1: Layihəni yaratmaq və kitabxanaları yükləmək
1 mkdir graphql-server
2 cd graphql-server
3 npm init -y
4 npm install apollo-server graphql

Addım 2: `index.js` faylını yaratmaq və kodu yazmaq

    1 // index.js
    2 
    3 const { ApolloServer, gql } = require('apollo-server');
    4 
    5 // 1. Schema (type definitions)
    6 // gql`` funksiyası GraphQL sxemini təhlil edir
    7 const typeDefs = gql`
    8   type User {
    9     id: ID!
10     name: String!
11   }
12
13   type Post {
14     id: ID!
15     title: String!
16     author: User!
17   }
18
19   type Query {
20     posts: [Post!]
21     users: [User!]
22   }
23 `;
   24 
   25 // 2. Məlumat Mənbəyi (sadəlik üçün mock data)
   26 const users = [
   27   { id: '1', name: 'Ayan' },
   28   { id: '2', name: 'Babek' },
   29 ];
   30 
   31 const posts = [
   32   { id: 'p1', title: 'GraphQL Möhtəşəmdir', authorId: '1' },
   33   { id: 'p2', title: 'REST-in sonu?', authorId: '2' },
   34   { id: 'p3', title: 'Apollo Server ilə başlamaq', authorId: '1' },
   35 ];
   36 
   37 // 3. Resolvers (Həll edicilər)
   38 // Resolver-lər Schema-dakı hər bir sahə üçün məlumatı necə əldə edəcəyini serverə bildirir.
   39 const resolvers = {
   40   Query: {
   41     // "posts" sorğusu gəldikdə bu funksiya işləyəcək
   42     posts: () => posts,
   43     // "users" sorğusu gəldikdə bu funksiya işləyəcək
   44     users: () => users,
   45   },
   46   Post: {
   47     // Hər bir Post obyekti üçün "author" sahəsini necə tapmaq olar?
   48     author: (parent) => {
   49       // "parent" obyekti hazırkı postdur (məsələn, { id: 'p1', title: '...', authorId: '1' })
   50       return users.find(user => user.id === parent.authorId);
   51     }
   52   }
   53 };
   54 
   55 // 4. Apollo Server-i yaratmaq
   56 const server = new ApolloServer({ typeDefs, resolvers });
   57 
   58 // 5. Server-i işə salmaq
   59 server.listen().then(({ url }) => {
   60   console.log(`🚀 Server hazırdir: ${url}`);
61 });

Addım 3: Serveri işə salmaq

1 node index.js
Terminalda 🚀 Server hazırdir: http://localhost:4000/ mesajını görəcəksiniz. Bu linkə daxil olsanız, Apollo Sandbox açılacaq. Bu, sorğularınızı test edə biləcəyiniz interaktiv bir mühitdir.

Sandbox-da bu sorğunu yoxlayın:
1 query {
2   posts {
3     id
4     title
5     author {
6       id
7       name
8     }
9   }
10 }

  ---

6. GraphQL-in Əsas Xüsusiyyətləri və Üstünlükləri

* Dəqiq Məlumat Tələbi: Over-fetching və under-fetching problemlərini aradan qaldırır.
* Tək Endpoint: Bütün sorğular adətən /graphql kimi tək bir endpoint-ə göndərilir. Bu, API idarəçiliyini asanlaşdırır.
* Güclü Tipləşdirmə (Strongly Typed): Schema sayəsində həm client, həm də server məlumatın formasını bilir. Bu, səhvləri hələ inkişaf mərhələsində aşkarlamağa kömək edir.
* Özünü Sənədləşdirmə (Self-documenting): Schema API-nin sənədləşməsi rolunu oynayır. Apollo Sandbox kimi alətlər bu sxemə əsasən avtomatik sənədləşmə yaradır.
* Versiyasız Təkamül (Evolving APIs without versioning): REST-dəki kimi /v1, /v2 versiyalarına ehtiyac yoxdur. Sadəcə olaraq sxemə yeni tiplər və sahələr əlavə edə bilərsiniz. Köhnə sahələri isə "deprecated" kimi
  işarələyərək tədricən ləğv etmək mümkündür.

  ---

7. GraphQL vs. REST: Müqayisəli Cədvəl


┌────────────────────┬─────────────────────────────────────────────────┬─────────────────────────────────────────────────────────────────┐
│ Xüsusiyyət         │ GraphQL                                         │ REST                                                            │
├────────────────────┼─────────────────────────────────────────────────┼─────────────────────────────────────────────────────────────────┤
│ Endpoint Strukturu │ Adətən tək bir endpoint (/graphql)              │ Çoxsaylı endpoint-lər (/users, /posts/:id)                      │
│ Məlumat Yükləmə    │ Client-in tələbinə uyğun dəqiq məlumat          │ Server tərəfindən müəyyən edilmiş sabit struktur                │
│ Protokol           │ HTTP üzərindən işləyir, protokoldan asılı deyil │ Adətən HTTP metodlarına (GET, POST, PUT, DELETE) bağlıdır       │
│ Tipləşdirmə        │ Güclü tipləşdirilmiş (Schema vasitəsilə)        │ Tipləşdirilməmiş (JSON cavabları üçün standart yoxdur)          │
│ Sənədləşmə         │ Avtomatik və interaktiv (Schema-dan yaranır)    │ Əlavə alətlər tələb edir (Swagger, OpenAPI)                     │
│ Versiyalaşdırma    │ Versiyasız təkamülə imkan verir                 │ Adətən URL-də versiya göstərilir (/api/v2)                      │
│ Caching            │ Mürəkkəbdir, çünki sorğular dinamikdir          │ Asandır, çünki URL-lər sabitdir və HTTP cache-dən istifadə edir │
└────────────────────┴─────────────────────────────────────────────────┴─────────────────────────────────────────────────────────────────┘

  ---

8. Nəticə və Növbəti Addımlar

GraphQL müasir tətbiqlərin mürəkkəb məlumat tələblərini qarşılamaq üçün güclü bir alternativdir. Xüsusilə fərqli client-lərin (veb, iOS, Android) fərqli məlumatlara ehtiyac duyduğu və ya mikroservis arxitekturasının
istifadə olunduğu layihələrdə çox effektivdir.

Öyrənmək üçün növbəti mövzular:
* Apollo Client / Relay: Client tərəfində GraphQL-i idarə etmək üçün güclü kitabxanalar.
* Authentication və Authorization: GraphQL-də istifadəçi icazələrinin idarə olunması.
* Caching və Performance: Sorğuların optimallaşdırılması və N+1 problemi.
* File Uploads: GraphQL ilə faylların yüklənməsi.
* Federation: Böyük miqyaslı layihələrdə bir neçə GraphQL servisinin birləşdirilməsi.