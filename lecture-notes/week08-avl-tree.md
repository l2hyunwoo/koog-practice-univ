# Week 8: AVL Tree

## 왜 AVL이 필요한가?
BST는 최악의 경우 편향 트리가 되어 O(n) 성능. AVL은 자동 밸런싱으로 항상 O(log n) 보장.

## 밸런스 팩터 (Balance Factor)
BF(node) = height(left) - height(right)
- |BF| <= 1 이면 균형
- |BF| > 1 이면 회전 필요

## 4가지 회전
| 불균형 유형 | 회전 | 설명 |
|---|---|---|
| LL (Left-Left) | Right Rotation | 왼쪽 자식의 왼쪽에 삽입 |
| RR (Right-Right) | Left Rotation | 오른쪽 자식의 오른쪽에 삽입 |
| LR (Left-Right) | Left → Right | 왼쪽 자식의 오른쪽에 삽입 |
| RL (Right-Left) | Right → Left | 오른쪽 자식의 왼쪽에 삽입 |

## BST vs AVL 비교
| 구분 | BST | AVL |
|------|-----|-----|
| 균형 보장 | X | O |
| 삽입 성능 | O(n) 최악 | O(log n) 보장 |
| 구현 복잡도 | 단순 | 회전 로직 필요 |

## 핵심 키워드
밸런스 팩터, LL/RR/LR/RL 회전, 높이 업데이트, 자동 밸런싱
