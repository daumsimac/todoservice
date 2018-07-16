# KAKAOPAY 사전과제

**개발환경**
- Backend
    - JAVA 8
    - Spring Boot 1.5.14
    - JPA
    - gradle
- Frontend
    - Bootstrap 4.0

**Build**
```
$ ./gradlew clean build
```

**Excute**
```
$ ./startup.sh
```

**API**

기본 구조
  * Data Type : JSON
  
| Property | Description | Etc                                                      |
|:----------|:-------------|:----------------------------------------------------------|
| code     | 응답코드    | 200, 400, 404와 같이 Http Status code로 처리결과를 나타냄|
| message  | HTTP Reason Phrase 메시지 |                                        |
| data     | 반환 데이터 |                                                          |
____

  * 할일 생성
  > URL : /api/v1/todos
  >
  > Method : POST
  >
  
  | Property | Data Type | Mandatory |
  |:---------|:----------|:----------|
  | content  | String    | Y  |
  | parent_id | Integer | N |
  ----
  
  * 할일 수정
  > URL : /api/v1/todos/{id}
  >
  > Method : PUT
  >
  
  | Property | Data Type | Mandatory |
  |:---------|:----------|:----------|
  | content  | String    | N  |
  | parent_id | Integer | N |
  ----
  
  * 할일 삭제
  > URL : /api/v1/todos/{id}
  >
  > Method : DELETE
  >
  ----
  
  * 할일 완료
  > URL : /api/v1/todos/{id}/complete
  >
  > Method : POST
  >
  ----
  
  * 할일 가져오기
  > URL : /api/v1/todos/{id}
  >
  > Method : GET
  >
  ----
  
  * 할일 리스트
  > URL : /api/v1/todos
  >
  > Method : GET
  >
  ----

**Usage**

## 1. 요구사항
#### 1. 1. TODO List 
```
1. TODO 추가시 다른 TODO를 참조로 걸 수 있다.
2. 참조는 다른 TODO의 ID를 명시하는 형태로 표현한다.
3. 사용자는 TODO를 수정할 수 있어야 한다.
  3.1 TODO의 내용을 수정할 수 있어야 한다(당연함).
  3.2 TODO의 참조를 변경할 수 있어야 한다.
    3.2.1 TODO의 참조를 끊을 수 있어야 한다.
    3.2.2 TODO의 참조를 다른 TODO로 변경 가능해야 한다.    
4. 사용자는 TODO를 완료처리 할 수 있다. 다만, 참조가 걸린 TODO가 완료되지 않았다면 완료처리가 불가능하다.
```
## 2. 접근방법
#### 2. 1. TODO List
```
1. 요구사항을 만족하려면 Tree형태로 TODO를 구성해야 한다.
2. DB로 Tree 구조를 나타내려면 다음과 같은 방법이 있다.
  2.1 인접목록 -> 한번에 자손을 불러올 수 있는 방법이 없음. 
    2.1.1 CTE (Common Table Expression) -> DBMS마다 지원여부가 다르며 같은 종류의 DBMS라도 Version마다 지원여부가 다르다.
  2.2 경로 열거 -> Node의 위상이 변해야 하는 경우 대응이 어렵다. (예전에 고생했던 기억이 있음...)
  2.3 중첩 집합 -> Node의 이동이나 추가가 빈번할 경우 복잡한 계산을 거쳐야 한다.
  2.4 클로저 테이블 -> Node를 저장하는 Table과 조상&자손 관계를 저장하는 테이블을 추가로 생성. Tree 계층 구조의 표현도 간단하며 조작도 간단한 편.
3. 요구사항에 명시된 TODO의 수정 기능은 단순히 내용의 수정만이 아닌 TODO의 참조를 변경할 수 있는 기능으로 판단
  3.1 사용하려는 DBMS가 CTE를 지원하고 추후 변경여지가 없다면 인접목록으로 설계하고 CTE를 사용하는 것이 가장 쉽고 빠름. 
      하지만, DBMS에 종속적인 query를 작성해야 하고 Oracle의 경우 version별로 다른 query를 만들어야 함. 유연하지 못해서 일단 제외.
  3.2 Table을 하나 더 생성해야 하는 단점이 있지만 연산이 비교적 단순하며 변경에 수월하게 대처할 수 있는 클로저 테이블이 적합한 모델이라고 판단.
```
#### 2. 2. Closer Table
```
집안일
|-----빨래
|-----청소
|       |-----방청소
```
| ID     |    TODO  |
|--------|----------|
| 1      | 집안일   | 
| 2      | 빨래     |
| 3      | 청소     |
| 4      | 방청소   |
----

| Ancestor | Descendant |
|----------|------------|
|     1    |     1      |
|     1    |     2      |
|     1    |     3      |
|     1    |     4      |
|     2    |     2      |
|     3    |     3      |
|     3    |     4      |
|     4    |     4      |
----
* 노드 추가 전략
  * 자기 자신을 참조하는 row를 tree_paths에 추가한다.
  * parent가 주어진 경우 parent의 모든 descendant를 자신의 ancestor로 자신의 ID는 descendant로 가지는 row들을 만들어서 tree_paths에 추가한다.
  * 이런 방식으로 tree_paths에 row를 추가하면 자신의 조상, 자손들을 모두 DB table로 표현 가능하며 한번의 query로 자신의 조상과 자손들의 ID를 얻어오는 것이 가능하다.
 
* 노드 삭제 전략
  * 자기 자신을 ancestor로 가지는 모든 row들과 자기 자신을 descendant로 가지는 모든 row들을 삭제한다.
  * 이렇게 하면 자기 자신을 포함한 자신의 descendant들을 모두 제거할 수 있다.

* 노드 이동 전략
  * 자기 자신을 descendant로 가지는 모든 row들을 제거한다. 단, 이때 자기 자신을 참조하는 row는 삭제하지 않는다.
  * 자신의 새로운 부모가 될 node를 descendant로 가지는 모든 row들과 자신을 ancestor로 가지는 모든 row들을 cross join하여 tree_paths에 추가한다.
  * 이 방법으로 기존에 있던 자신의 tree path 정보는 모두 삭제하고 새로운 tree path 정보를 저장할 수 있다.
  * 자기 자신의 자손의 하위 노드로 이동은 불가능하다.

* 경로열거를 응용하여 화면에 표시할 할일을 따로 저장
  * 요구사항에 나온대로 참조가 걸린 Parent를 화면에 표시하기 위해서 다음과 같은 방법이 있을 수 있다.
    * Listing API에서 각 entry에 ancestor의 ID를 주고 Client에서 알아서 처리하라고 한다. -> 경험상 클라이언트 개발자들이 싫어함.
    * 매번 조회할 때 마다 부모의 정보를 얻어와서 문자열을 조합하여 응답을 보내준다.
    * todos table에 경로열거 방식으로 display_content column을 추가하고 생성, 부모 변경과 같은 작업이 일어날 때 자신을 root로 하는 subtree의 display_content를 갱신.
  * 세번째 방법을 선택하기로 하였다. 이유는 부모 변경은 자주 발생할 것 같지 않으며 생성할 때 
     자신의 조상들의 정보를 얻어서 저장해뒀다가 조회시 단순히 보여주는 것이 매번 조회 시 자신의 조상들을 조회하여 문자열을 만드는 것 보다
     효율적이라고 생각하였다.

#### 2. 3. Persistent Layer 구현

* EntityManager를 이용한 Native Query 작성
  * 가급적 JPA를 이용하여 ORM으로 처리하는 것을 처음 설계를 하였을 때 원칙으로 하였다.
  * Closer Table을 ORM으로 표현하려고 하다 보니 난처한 점들이 생겼다.
    * JPQL은 UNION을 지원하지 않는다.
    * JPQL은 INSERT INTO SELECT도 지원하지 않는다.
    * ~~DELETE하는 건..그냥 넘어가자.~~
    * 위 두가지 이유로 tree_path table의 repository는 모두 native query로 처리하였다.
    * JPQLTemplates에서 group_concat을 지원하지 않는다.
    * 위 이유로 todos table의 display_content를 생성하는 기능을 native query로 처리하였다.
* QueryDSL을 이용하여 자손들 중 완료되지 않은 할일을 찾거나 Subtree root를 기준으로 모든 자손들을 찾는 기능 작성
  * JPA Repository에서 제공하는 기능만으로는 두개의 table을 참조하여 원하는 기능을 얻기 어렵다고 판단하였다.
  * QueryDSL을 이용하여 구현

```
가급적 쿼리에 로직이 들어가는 것은 피해야 한다고 생각한다 
하지만, 이번 Closer Table 같은 경우는 DB를 이용하여 Tree라는 자료구조를 표현하는것이기 때문에 Tree에 연관된 로직을
구현하는 것으로 제한한다면 괜찮은 접근방법이 될 것이라 생각하여 위와 같이 Closer Table 접근부를 작성하였다.
```

## 3. 추가 기능 정의

* 완료한 할일은 다시 미완료로 되돌릴 수 없다.
* 할일을 삭제하면 참조가 걸린 할일들(descendant node)을 모두 삭제한다.
* 이미 완료한 할일을 참조로 걸 수 없다.

## 4. 실제 Frontend에서 구현한 부분

* 할일 추가
* 할일에 참조를 걸어서 추가
  * 이미 완료한 할일을 참조로 걸 수 없음.
* 할일 완료
* 할일 Listing
* 할일 수정
  * X-Editable을 이용하려 했으나 설정같은거 살펴보고 붙이려고 하니 시간이 부족했음.
  * 그냥 수정 버튼 만들고 Modal Popup 뛰워서 처리하는 걸로 계획 변경.
* 할일 삭제

## 5. Future Works

* Tree 구조에 대한 CRUD Library Class 작성
```
지극히 개인적인 핑계지만 평일에는 시간이 거의 나지 않아서 주말 이틀에 몰아서 작업해야하고 나한테는 매우 서투른
Frontend 관련 작업까지 하려다 보니 Backend쪽 작업에 조금 소홀했던 면이 있다. 그중 특히 Tree 관련 연산하는 부분이 매우 마음에 들지 않는다.
시간이 좀 더 있으면 저 부분 필요한 Interface 정의하여 Library Class로 분리하고 새로 구현한 Class를 이용하도록 서비스를 구현해보고 싶다.
```

* Listing API 개선
```
현재 Listing API는 그냥 flat하게 모든 자료를 가져와서 보여주는 형태이다. 하지만, 내부적으로는 부모/자식 관계, 조상/자손 관계를 가지고 있으므로
Frontend에서 제대로 List를 보여주려면 Tree 형태로 Listing API가 구성되어야 할 필요가 있다. 
```

* Frontend에서 할일의 부모 변경 기능 추가
```
Frontend 작업에 미숙한 점과 시간문제까지 더해져서 Frontend에서 할일의 부모 변경 기능을 넣지 못하였다. 좀 더 쓸만한 어플리케이션이 되려면
할일의 부모 변경 기능이 있어야 할 것 같은데 API에서는 지원하지만 Frontend에 붙이지 못한 점이 아쉽다.
```

* 부모 변경시 버그 수정
```
현재 구현에서 Subtree를 떼어낼 수 없으며 같은 부모로 이동하는 동작에 대해서 무시하지 못하는 버그가 존재한다.
자신의 직계 조상을 todos에 저장하고 이를 이용하면 쉽게 구현이 가능할 것 같다. 
```