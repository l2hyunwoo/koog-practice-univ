# 과제 3: AVL Tree 구현

## 과목
자료구조 (Data Structures)

## 요구사항
C++로 AVL Tree를 구현하시오.

### 필수 구현 사항
1. Node 구조체 (key, height, left, right)
2. 높이 계산 함수 (getHeight)
3. 밸런스 팩터 계산 함수 (getBalance)
4. 오른쪽 회전 (rightRotate)
5. 왼쪽 회전 (leftRotate)
6. 삽입 함수 (insert) — 자동 밸런싱 포함
7. 중위 순회 (inorderTraversal)

### 테스트 케이스
다음 순서로 삽입 후 중위 순회 결과를 출력하시오:
입력: 10, 20, 30, 40, 50, 25
기대 출력: 10 20 25 30 40 50

### 제출 형식
- 소스 파일: avl_tree.cpp
- 컴파일: g++ -o avl avl_tree.cpp
- 제출 기한: 5월 16일 (금)
