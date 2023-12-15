```sh
file a.txt 查看文件属性 文件类型
ls -a 
ln -s a b 创建软连接  windows 快捷方式
ln a b 硬链接  当所有硬链接删除以后，文件内容就会被删除  每个硬链接都是一个指向文件索引的一个指针，这个文件没有任何硬链接指向它的时候他才会被删除
```

```sh
chmod u+x test.xml 给当前用户添加权限 可执行权限
	  g  所属用户组
	  
-rwxr--r--  1 root root    0 Dec 14 21:39 test.xml*  
其中的w是可写 x是可执行  r是可读
有三队是表示 所属主  所属组 其他人
```

权限数字

| read | write | execute |
| ---- | ----- | ------- |
| 4    | 2     | 1       |

 他们不同的组合对应着唯一的数字

```sh
 chmod 722 test.xml  //给该文件 所属主添加rwx权限，给所属组与其他只设置为可读 
```

wc命令

```
wc -l test.sh //统计行数
 
wc -w test.sh //统计字符串
 
wc -m test.sh //统计字符数
```

