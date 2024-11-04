## GDBMS Visual-Tool For TurboGraph++ 

ViT (Visual Tool)는 대화형 환경에서 그래프 DBMS에 질의를 수행하고 결과를 시각적으로 표현하고 분석할 수 있는 위한 프로그램이다.

ViT는 다양한 DBMS를 지원하는 오픈 소스 도구인 [DBeaver v21.2.2](https://github.com/dbeaver/dbeaver) 기반으로 그래프 DBMS의 결과를 시각화하기 위해 [JavaFXSmartGraph](https://github.com/brunomnsilva/JavaFXSmartGraph) 등의 추가 오픈 소스를 사용하여 개발되었다.

그래프 DBMS를 위해 추가된 기능은 다음과 같다.

* 연결 부분 : 그래프 DBMS 연결(TurboGraph++, Neo4j Graph) 및 연결 테스트
* 네비게이션 부분 : 연결된 그래프 DB의 정점과 간선 정보 표시
* 질의 창  : 연결된 그래프 DB의 질의 작성 및 요청
* 시각화 창
   - 질의의 결과를 그래프로 시각화 표현 (정점, 간선 및 레이블 표시)
   - 그래프 편집 (이동, 하이라이트, 속성 변경 등)
   - 미니맵 
   - 레이아웃 변경
   - 분석 기능 (shortest path 등) 

개발 관련 문서등은 ViT_docs 폴더에서 관리된다.

## 시작하기

ViT는 리눅스 환경에서 소스 코드를 다운로드 받은 후 빌드 스크립트를 통해 빌드할 수 있다.

Vit 사용 방법등은 [사용자 메뉴얼](https://hwany7seo.github.io/vit_manual/start.html)을 참고 할 수 있다.

## 소스 다운로드

```
git https://github.com/postech-dblab-iitp/visual-tool.git
```

## 프로그램 빌드

### 빌드 요구 사항

현재 ViT의 빌드는 Linux 환경에서만 지원한다.

빌드에 필요한 프로그램은 다음과 같다

 - JDK 11
 - Apache Maven 3.8.6+
 - git
 - 인터넷 연결


## 빌드 실행 방법

```
git clone https://github.com/postech-dblab-iitp/visual-tool.git
cd visual-tool
sh build.sh
```

## 참고 사항

- 사용자 메뉴얼 및 개발 가이드
    - https://hwany7seo.github.io/vit_manual

## 라이센스

- Apache license 2.0 (자세한 라이선스 정보는 LICENSE.MD 파일을 참조하세요.)
  

### 프로젝트 디렉토리

- ViT_Manual/ : rst 형태의 ViT 메뉴얼
- ViT_docs/ : ViT 설계 문서
- bundles/ : 기본 플러그인
- docs/ : 원본 DBeaver 문서
- features/ : 프로그램의 plugin, dependencies를 구조화 하는데 사용
- plugins/ : 원본 소스, 상세 내용은 DBeaver wiki 참조 ( https://github.com/dbeaver/dbeaver/wiki/Develop-in-Eclipse) 
- product/ : 최종 프로그램의 설정
- test/ : 원본 DBeaver test 코드

## 도움 받기

http://jira.iitp.cubrid.org/secure/Dashboard.jspa

버그, 개선 사항, 질문이 있는 경우 위 jira에 내용을 남기면 지원을 받을 수 있다.

## 사용 및 참고 
### DBeaver
- https://github.com/dbeaver/dbeaver
### For Graph Visualize
- https://github.com/brunomnsilva/JavaFXSmartGraph
- https://github.com/rayjasson98/Java-Graph-Algorithms-Visualizer