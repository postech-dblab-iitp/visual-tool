[![English](
https://img.shields.io/badge/language-English-orange.svg)](README_EN.md)
[![Korean](
https://img.shields.io/badge/language-Korean-blue.svg)](README.md)

## GDBMS Visual-Tool For TurboGraph++ 

IITP-차세대 DBMS 과제 중 GDBMS를 위한 시각화 도구를 개발하기 위한 것이다.    
시각화 도구는 DBeaver( https://github.com/dbeaver/dbeaver )를 Base (v21.2.2)로  
Gephi 등 Open Source Lib를 사용하여 확장하여 개발하는 것을 목표로 한다.  
개발 관련 문서등은 ViT_docs 폴더에서 관리된다.

## 시작하기

IITP GDBMS Visualization 도구 (이하 ViT)의 소스는 해당 github에서 다운로드 할 수 있으며 빌드에 사용되는 스크립트도 포함되어 있다.

### 소스 획득

```
git https://github.com/Kang-dot/iitp_visual_tool.git
```

### 프로그램 빌드

빌드는 리눅스 환경에서 진행해야 한다.
```
sh build.sh
```

### 프로젝트 디렉토리

- ViT_Manual/ : rst 형태의 ViT 메뉴얼
- ViT_docs/ : ViT 설계 문서
- bundles/ : 기본 플러그인
- docs/ : 원본 DBeaver 문서
- features/ : 프로그램의 plugin, dependencies를 구조화 하는데 사용
- gephi-toolkit/ : 그래프 표시에 사용되는 시각화 라이브러리
- plugins/ : 원본 소스, 상세 내용은 DBeaver wiki 참조 ( https://github.com/dbeaver/dbeaver/wiki/Develop-in-Eclipse) 
- product/ : 최종 프로그램의 설정
- test/ : 원본 DBeaver test 코드
