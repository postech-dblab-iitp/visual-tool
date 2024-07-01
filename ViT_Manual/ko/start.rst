
:meta-keywords: graph tools
:meta-description: Chapter contains useful information on starting Program.

*************
ViT 시작하기
*************

ViT로 DB(Neo4j) 연결/시작
=============================================

본 프로그램을 처음 사용하는데 참고 할 수 있는 간략한 사용법을 설명하며, 해당 화면들은 Windows 10 운영체제에서 실행 된 화면이다.

현재 TurboGraph++, Neo4J 두 가지로 접속 할 수 있도록 구성되어 있으며, 
3년차인 현재 메뉴얼에서는 Neo4j에 생성된 DB를 사용하여 진행 과정을 소개한다.


프로그램을 실행하면 WelCome 이미지가 나타나고 프로그램이 시작된다.

.. image:: /images/start/splash.png
 
다음은 Database가 연결된 항목이 아무것도 없을 경우 발생 되는 초기화면이다. 
초기화면에서 database를 선택하여 연결/추가할 수 있다.

.. image:: /images/start/s1.png

------------------------------
Database 연결
------------------------------

초기화면에서 TurboGraph++를 선택 후 [Next]를 동작하면 아래와 같이 Neo4j Connection 설정 화면으로 이동된다.
TurboGraph++ (New)로는 TurboGraph++로 Connection 할 수 있다.
추후 TurboGraph++만 지원 예정이다.

.. image:: /images/start/selectdatabase.png

Host : Host Name 또는 IP를 입력한다.

Port : 접속 하려는 Bolt 포트를 입력한다. {Neo4j Bolt Port에 기본값은 7687이다.}

Database/Schema : 접속하려는 DB 이름을 입력한다.

UserName, Password를 입력 한 후
[Test Connection] 선택하며 접속 테스트를 할수 있으며,
[Next] 누르면 Database Navigator(연결 탐색기)에 추가된다.

연결 테스트
------------------------------

Test Connection 버튼을 선택하면 아래와 같이 Driver가 없을 경우에는 
자동으로 Driver DownLoad Page로 이동되고 미리 입력된 URL에서 download 받을 수 있다.

.. image:: /images/start/s3.png
  
아래와 같이 Download configuration을 재설정하여 다른 URL에서도 Driver를 Download 받을 수 있다.

.. image:: /images/start/s3-1.png
  
정삭적으로 연결 되었을 경우 아래와 같은 Popup을 통해 결과가 표시된다 
[OK]를 선택하면 화면이 종료되고 [Details>>] 버튼을 누르면 자세한 내용을 확인 할 수 있다.

DataBase 추가
------------------------------

.. image:: /images/start/s4.png
  
연결 테스트 완료 후에 [Finish]를 선택하면 아래와 같이 DataBase가 추가되고,

Database Navigator(연결 탐색기)에서 [v]을 통해 Tree를 확장하여 Node Type, Edge Type등을 확인 할 수 있다.

.. image:: /images/start/s5.png
  
------------------------------
SQL(GQL) 편집기
------------------------------

아래와 같이 SQL(GQL) 편집기 메뉴를 통해서 Query를 입력창 열 수 있다.

.. image:: /images/start/s6.png
  
Query 입력창을 통해서 Query를 입력할 수 있으며, 입력 후 [>]{SQL(GQL) 실행} 버튼을 통해 Query를 실행 할 수 있다.

.. image:: /images/start/s7.png

------------------------------
데이터 결과창
------------------------------

이후 시각화 View가 표시되며 Query에서 결과 테이터를 시각화하여 표시된다.
간선은 1:N, Self-loop 간선 등을 지원한다.

.. image:: /images/start/s12.png
  
또한 결과 데이터는 [Visualization]{시각화 뷰}외에 

그리드, Console, 텍스트 형태로 확인이 가능하다.

.. image:: /images/start/s9.png


.. image:: /images/start/s10.png


.. image:: /images/start/s11.png
  

Mini Map
------------------------------

아래 화면에서 빨간색 표시부분 [MiniMap] 버튼을 사용하면 MiniMap을 열거나 닫을 수 있다.
추가 기능 사항으로 미니맵에 포인트 표시 및 이동 기능이 추가 되어 
미니맵 내 마우스 클릭 시 이동 및 포인트 표시가 가능하다.

.. image:: /images/start/s18.png

확대 축소 하기
------------------------------

아래 화면에서 노란색 표시부분 [+],[-] 버튼을 사용하면 확대/축소가 가능하다.
또한 < Ctrl + 키보드+ >, < Ctrl + 키보드- > 를 통해서도 확대/축소가 가능하다.

.. image:: /images/start/s19.png

HighLight 설정
------------------------------

Node를 마우스로 두번 클릭하면 선택 된 정점이 빨간색 테두리를 통해 표시된다.
마우스 오른쪽 버튼으로 팝업 메뉴를 활성화 할 수 있다.
이후 [HighLight]를 선택하면 Node와 연결 된 Edge 및 Target Node가 HighLight되어 표시 된다.

.. image:: /images/start/s14.png
  
.. image:: /images/start/s15.png
  
HighLight 해제
------------------------------

HighLight를 해제 하기 위해서는 마우스 오른쪽 버튼은 팝업 메뉴를 활성화 한 후
[unHighLight]를 선택하거나 다른 정점을 두번 클릭하여 선택하면 해제 된다.

.. image:: /images/start/s16.png
  
시각화 편집 기능
------------------------------

HighLight 기능과 마찬가지로 팝업 메뉴 활성화하여 시각화 된 화면에 정점 및 간선 삭제 기능이 제공된다.
Undo, Redo를 통해 최대 5개 까지 저장하여 삭제 및 복원이 가능하다.

.. image:: /images/start/s21.png

표시 데이터 개수 설정
------------------------------

아래 화면에서 노란색으로 표시된 값에 따라 결과 Data Row 개수를 설정 할 수 있으며,
Default 값는 200이다. PC 성능에 따라 개수를 조절하여 사용 할 수 있다.

.. image:: /images/start/s20.png


기타 기능
=============================================

------------------------------
Layout 정렬 기능
------------------------------

-	시각화 된 결과를 총 5가지 형식으로 정렬하는 기능
1.	Horizontal Tree (수평 가지형태로 표시)
2.	Vertical Tree (수직 가지 형태로 표시)
3.	Grid (일정한 간격으로 격자 형태로 표시)
4.	Circle (그룹 별로 묶어 원형 형태로 표시)
5.	Spring (서로 붙지 않도록 스프링처럼 서로 미는 형태)

.. image:: /images/start/layout_horizontal_tree.png
  :alt: Horizontal Tree

*Horizontal Tree*

.. image:: /images/start/layout_grid.png
  :alt: Grid

*Grid*

.. image:: /images/start/layout_circle.png
  :alt: Circle

*Circle*

.. image:: /images/start/layout_spring.png
  :alt: Spring

*Spring*

------------------------------
최단 경로 구하기
------------------------------
최단 경로(Shotest Path) 기능은 두 정점을 선택하여 최단 경로를 구하는 기능이다.

빨간색 표시 메뉴를 선택하면 노란색 창이 나타나며,
이후 첫번째 노드를 더블 클릭한 후 노란책 창에 콤보 박스로 이용하여 가중치를 선택 할 수 있다.
가중치 선택은 Default(가중치 1)와 Property 값들이다.

Default일 경우 모든 경로 당 가중치를 1로 계산하여 경로가 표시되고,

Property를 선택 할 경우 Property 타입이 정수 일 경우 해당 Property의 정수 값을 가중치로 계산하여 경로가 표시된다.
Property 값이 정수가 아닐 경우에는 가중치 1로 계산된다.

.. image:: /images/start/shortest_path.png

------------------------------
Chart 기능
------------------------------

Chart 기능은 정점과 속성을 선택하여 그래프에 표시 된 정점을 대상으로 분포도를 확인 할 수 있는 기능이다.

빨간색 표시된 버튼을 통해 Chart 기능을 활성화 할 수 있고,

노란색 표시된 Chart 창을 통해 정점과 속성을 선택하여 분포도를 확인 할 수 있다.


.. image:: /images/start/chart.png

------------------------------
Value 창으로 세부 정보 확인
------------------------------

빨간색 표시된 버튼을 통해 Value 창 표시 할 수 있으며,

마우스를 통해 정점, 간선를 한번 클릭하면 선택 된 정점 또는 간선 정보 (ID, Label, Type, Propery 값)를 확인 할 수 있다.

행을 선택 후 오른쪽 마우스 버튼으로 Context 메뉴를 활성화 하면 값 또는 이름을 복사 할 수 있는 메뉴가 나타난다.


.. image:: /images/start/s13.png


------------------------------
디자인 편집 기능
------------------------------

빨간색 표시된 버튼을 통해 디자인 편집 기능을 활성화 할 수 있으며,
노란색으로 활성화 된 창을 통해 정점의 Label, 간선의 Type을 그룹별로 디자인 편집이 가능하다.

정점은 크기, 색상, 글자 크기등을 변경 할 수 있고 
정점 내부에 표시되는 글자는 ID, LABEL, Property를 선택하여 보여지는 글자를 변경 할 수 있다.

간선은 간선의 모양, 두께, 색상, Type의 글자 크기를 변경 할 수 있다.

.. image:: /images/start/design_edit.png

------------------------------
Capture 하기
------------------------------

아래 화면에서 노란색으로 표시된 [사진기] 버튼을 통해 시각화 View 내용을 그림파일로 저장할 수 있다.

.. image:: /images/start/s17.png
  
------------------------------
결과 데이터 CSV 파일로 내보내기
------------------------------

빨간색으로 표시 된 버튼을 통해 csv 파일을 저장할 경로와 이름을 설정할 수 있다.
설정 완료 후 [OK]를 누르면 CSV 파일로 저장이 완료 된다.

.. image:: /images/start/export_csv.png

------------------------------
질의 변환기
------------------------------

TurboGraph++ 연결 시 아래 화면에 표시 된 것 처럼 질의편집기 상단에 질의 변환기가 표시된다.

전체 정점의 라벨과 속성, 간선의 타입과 속성을 선택 할 수 있으며,
지원하는 비교 연산자과 값을 입력 후 빨간 색으로 표시된 버튼을 사용하면
간단한 Cypher 질의로 변환 할 수 있다.

.. image:: /images/start/query_tran.png
