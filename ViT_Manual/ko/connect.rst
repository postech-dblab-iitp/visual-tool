
:meta-keywords: graph tools
:meta-description: Chapter contains useful information on starting Program.

*************
Database 연결
*************

초기화면에서 TurboGraph++를 선택 후 [Next]를 동작하면 아래와 같이 Neo4j Connection 설정 화면으로 이동된다.
각 Database를 선택하여 Connection 설정 할 수 있다.

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

.. image:: /images/start/driver_download.png
  
아래와 같이 Download configuration을 재설정하여 다른 URL에서도 Driver를 Download 받을 수 있다.

.. image:: /images/start/driver_preferences.png
  
정삭적으로 연결 되었을 경우 아래와 같은 Popup을 통해 결과가 표시된다 
[OK]를 선택하면 화면이 종료되고 [Details>>] 버튼을 누르면 자세한 내용을 확인 할 수 있다.

Database 추가
------------------------------

.. image:: /images/start/test_connection.png
  
연결 테스트 완료 후에 [Finish]를 선택하면 아래와 같이 DataBase가 추가되고,

Database Navigator(연결 탐색기)에서 [v]을 통해 Tree를 확장하여 Node Type, Edge Type등을 확인 할 수 있다.
TurboGraph++는 Node, Edge의 Type 및 Propeties를 모두 확인 할 수 있으며,
Neo4j는 Node Type 정보만 표시된다.

.. image:: /images/start/navi_type_property.png