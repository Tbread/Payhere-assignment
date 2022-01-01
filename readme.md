#Pageonce

2022년 1월 2일 기준

http://34.64.84.146:8080

에서 테스트 가능하며

해당 프로젝트 소스이용시 application.properties 에서db 경로와 db 사용자이름,암호를 입력해야 합니다.

## DB설계

### User

- id (pk,long)
- email (이메일, string)
- password (암호, string)
- createdAt (가입일, datetime)
- modifiedAt (최근 회원정보 수정일, datetime)

### Pageonce(가계부)

- id (pk)
- expenditure (지출금액, long)
- memo (메모, string)
- deleted (삭제 여부, boolean)
- userId (작성자 pk, long) / 따로 User와 매핑하지 않음
- createdAt (가계부 작성일, datetime)
- modifiedAt (최근 가계부 수정일, datetime)

####DDL 파일은 src 폴더내 pageonce.sql 로 첨부되어있습니다.

## API

### 모든 정보는 json타입으로 송/수신하며 아래와 같이 서술합니다.

### [정보설명 (데이터타입) - json 키값명/필수여부]

#### UserController

1. 회원가입 ( /register )
    - PostMapping
    - 요구 정보
        1. 이메일 (string) - email / 필수
        2. 암호 (string) - password / 필수

    - 응답
        1. 성공여부 (boolean) - success
        2. 메세지 (string) - message
        3. 가입성공시 가입한 이메일 (string) - email


2. 로그인 ( /login )
    - PostMapping
    - 요구 정보
        1. 이메일 (string) - email / 필수
        2. 암호 (string) - password / 필수
    - 응답
        1. 성공여부 (boolean) - success
        2. 메세지 (string) - message
        3. 로그인 성공시 로그인한 이메일 (string) - email
        4. JWT 토큰 (string) - token

#### PageonceController

1. 가계부 작성 ( /write )
    - PostMapping
    - 요구 헤더
        1. JWT 토큰 (string) - Authorization / 필수
    - 요구 정보
        1. 지출금액 (long) - expenditure / 필수
        2. 메모 (string) - memo / 선택

    - 응답
        1. 성공여부 (boolean) - success
        2. 메세지 (string) - message
        3. 작성 성공시 작성한 지출금액 (long) - expenditure
        4. 작성 성공시 작성한 메모 (string) - memo


2. 가계부 상세 내역 확인 ( /view/{확인할 가계부의 id} )
    - GetMapping
    - 요구 헤더
        1. JWT 토큰 (string) - Authorization / 필수

    - 응답
        1. 성공여부 (boolean) - success
        2. 메세지 (string) - message
        3. 지출금액 (long) - expenditure
        4. 메모 (string) - memo
        5. 작성일 (datetime) - created_at
        6. 마지막 수정일 (datetime) - modified_at


3. 가계부 리스트 확인 ( /view )
    - GetMapping
    - 요구 헤더
        1. JWT 토큰 (string) - Authorization / 필수

    - 응답
        1. 성공여부 (boolean) - success
        2. 메세지 (string) - message
        3. 지출금액,메모가 담긴 어레이리스트 (Array<long,string>) - expenditure / memo


4. 가계부 수정 ( /modify/{수정할 가계부의 id} )
    - PatchMapping
    - 요구 헤더
        1. JWT 토큰 (string) - Authorization / 필수
    - 요구 정보
        1. 지출금액 (long) - expenditure / 필수
        2. 메모 (string) - memo / 선택

    - 응답
        1. 성공여부 (boolean) - success
        2. 메세지 (string) - message
        3. 수정 성공시 수정된 지출금액 (long) - expenditure
        4. 수정 성공시 수정된 메모 (string) - memo


5. 가계부 삭제 ( /delete/{삭제할 가계부의 id} )
    - PatchMapping
    - 요구 헤더
        1. JWT 토큰 (string) - Authorization / 필수

    - 응답
        1. 성공여부 (boolean) - success
        2. 메세지 (string) - message

6. 가계부 복구 ( /restore/{복구할 가계부의 id} )
    - PatchMapping
    - 요구 헤더
        1. JWT 토큰 (string) - Authorization / 필수

    - 응답
        1. 성공여부 (boolean) - success
        2. 메세지 (string) - message

## 테스트 코드

### UserControllerTest

- 10가지 케이스
    1. 회원가입-정상
    2. 회원가입실패-이메일누락
    3. 회원가입실패-패스워드누락
    4. 회원가입실패-이메일형식
    5. 회원가입실패-모두누락
    6. 회원가입실패-중복이메일
    7. 로그인-정상
    8. 로그인실패-이메일누락
    9. 로그인실패-패스워드누락
    10. 로그인실패-모두누락

### PageonceControllerTest

- 24가지 케이스
    1. 가계부작성-정상1
    2. 가계부작성-정상2,메모누락
    3. 가계부작성실패-비로그인
    4. 가계부작성실패-지출누락
    5. 가계부상세조회-성공
    6. 가계부상세조회실패-잘못된id
    7. 가계부상세조회실패-삭제된 가계부
    8. 가계부상세조회실패-다른유저의가계부
    9. 가계부수정-정상1
    10. 가계부수정-정상2,메모누락
    11. 가계부수정-실패1,비로그인유저
    12. 가계부수정-실패2,잘못된 ID
    13. 가계부수정-실패3,삭제된 가계부
    14. 가계부수정-실패4,타인의 가계부
    15. 가계부수정-실패5,금액 누락
    16. 가계부삭제-정상
    17. 가계부삭제-실패1,비로그인
    18. 가계부삭제-실패2,잘못된 ID
    19. 가계부삭제-실패2,삭제된 가계부
    20. 가계부삭제-실패2,타인의 가계부
    21. 가계부복원-정상
    22. 가계부복원실패-비로그인
    23. 가계부복원실패-삭제되지않은 가계부
    24. 가계부복원실패-타인의 가계부