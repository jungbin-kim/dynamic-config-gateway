# Dynamic Route Gateway

# 1번 시도
* branch: `try1`
* Spring Config Server/Client 사용
* Spring Config Server 에서 Spring Cloud Gateway(Client)의 설정을 가진다.
## 준비
### config git repository
* 이 프로젝트 외 다른 local git repository 로 테스트 
* spring cloud gateway config 파일을 가지고 있다.
* `gw-dev.yml` 
    * 네이밍 룰: `${service name}-${service profile}.yml`
    * 초기 파일
    ```yml
    spring:
      cloud:
        gateway:
          routes:
            - id: route1
              uri: http://localhost:8081
              predicates:
              - Path=/hello1.txt
              - Method=GET
            - id: route2
              uri: http://localhost:8081
              predicates:
              - Path=/hello2.txt
              - Method=GET
    ```

### config-backend
* 참조할 config 들을 가지고 있는 path 를 환경변수로 넣어준다.
```
configGitPath=${local git repository path}
```

### gw-backend
* active profile 을 `dev`

## 실행
* Run `config-backend`
    * GET `http://localhost:9100/gw/dev` 로 `gw-dev.yml` 설정을 제대로 가져오는지 확인
    * `config git repository`의 `gw-dev.yml`를 변경하고 변경 사항 반영하는지 확인
* Run `gw-backend`
    * GET `http://localhost:8081/actuator/gateway/routes` 로 현재 routes 확인
    * GET `http://localhost:8081/hello1.txt` 로 라우트 되는지 확인
        * 실제 gateway 가 라우팅 목적지가 없음으로 error 발생하지만, 라우트 설정이 있을 때와 없을 때가 다르다.
    * `gw-dev.yml` 수정
    * POST `http://localhost:8081/actuator/refresh` 으로 `gw-backend`에 변경된 설정 적용

## 결과
동적으로 gateway의 설정이 바뀌지만, 대량 수정이 있을 경우 느려진다.
    
참고: [What happens to current requests when /actuator/refresh endpoint called](https://github.com/spring-cloud/spring-cloud-gateway/issues/1619)

# 2번 시도
* redis 를 사용. downstream url 을 redis 에서 가져와서 사용.
* redis 의 key는 gateway의 sub-domain
    * ex) gw-backend 에 test.jungbin.kim:8081 로 접근하면 
    sub-domain인 test 를 key로 하여 redis 에서 downstream server url 을 가져온다.  
## 준비
* docker
```sh
# redis 최신 이미지 다운로드
$ docker pull redis

# docker redis container 실행
$ docker run --name gw-redis -d -p 6379:6379 redis

# docker redis container 내부 진입
$ docker exec -it gw-redis /bin/bash

# (docker container 내부) redis-cli 실행
$ redis-cli

# 데이터 저장. input-key 를 sub-domain 으로 치환
127.0.0.1:6379> set input-key "{\"targetUrl\":\"https://dev.jungbin.kim\"}"
# 필요시, flushall 명령어를 사용하여 redis 저장소 초기화
```

* 로컬 테스트를 위해서 로컬 도메인 세팅을 해준다.
    * mac hosts 파일 수정

## 실행
* Run downstream server
* Run `gw-backend`
* Call `input-key.jungbin.kim:8081/downstream/path`