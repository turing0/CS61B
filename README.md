# CS61B
 UC Berkeley CS61B, Spring 2021. 

课程主页：https://sp21.datastructur.es/

## 开始教程

注册gradescope来使用autograder

1. 课程入门代码：MB7ZPY

2. 学校： UC berkeley

3. 学生卡：不用填

拉取作业存储库

```text
git clone https://github.com/Berkeley-CS61B/skeleton-sp21
```

## 可能遇到的问题

### Proj0

如果你的电脑系统是中文，那么在 proj0 的 2048 游戏界面中按键会没有反应，这是因为是系统是中文的原因，得修改一处代码。

TL;DR:

在 `GUISource` 这个文件 `36` 行位置开始：

```java
String command = _source.readKey();
switch (command) {
    case "W" :
        command = "Up";
        break;
    case "D" :
        command = "Right";
        break;
    case "S" :
        command = "Down";
        break;
    case "A" :
        command = "Left";
        break;
    default :
        break;
}
```

将代码修改成上面那样，上下左右箭头改成了WASD键（注意，输入英文默认的是大写字母）。

你也可以修改为上下左右的箭头，像下面这样：

```java
String command = _source.readKey();
switch (command) {
    case "向上箭头" :
        command = "Up";
        break;
    case "向右箭头" :
        command = "Right";
        break;
    case "向下箭头" :
        command = "Down";
        break;
    case "向左箭头" :
        command = "Left";
        break;
    default :
        break;
}
```
