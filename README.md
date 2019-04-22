# android-Camera
![](https://i.imgur.com/AMpK27h.jpg)
ui比较low 支持单拍，连拍，闪光灯 摄像头切换，自动保存，修复第一次进入是黑屏，以及拍摄一次之后停留在预览画面
实现 思路

1，依据布局，获取Surfaceview对象；
 
2，获取Holder对象并设置属性；
 
3，绑定SurfaceHolder.Callback回调接口；
 
4，回调接口的surfaceCreated方法中设置Camera并设置对应属性；
 
5，设置拍照点击事件，在监听事件中绑定Camera.PictureCallback回调监听；
 
6，PictureCallback监听中依据回调数据，进行结果保存。【保存照片并展示】