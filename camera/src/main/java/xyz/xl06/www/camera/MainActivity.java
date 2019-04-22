package xyz.xl06.www.camera;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private static File fileFolder;
    @BindView(R.id.surface)
    SurfaceView surface;
    @BindView(R.id.opean)
    Button opean;
    @BindView(R.id.show)
    ImageView show;
    @BindView(R.id.shan)
    Button shan;
    @BindView(R.id.huan)
    Button huan;
    private SurfaceHolder holder;//处理画面质量
    private Camera camera;
    private Camera.Parameters parameters;//画面参数
    private boolean isShan=false;
    private List<Integer> mWaitAction = new LinkedList<>(); //暂存拍照的队列
    private boolean isTaking = false;   //是否处于拍照中
    private int iCameraCnt;
    private int  iFontCameraIndex = 1;//后置摄像头
    private int  iBackCameraIndex = 2;//前置摄像头
    private boolean bBack=false;//默认摄像头
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        //获取画面的质量
        holder = surface.getHolder();
        //添加回调函数
        holder.addCallback(new MyCallSurface());
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surface.setVisibility(View.VISIBLE);
    }

    /**
     * 判断是否连续拍照
     */
    public void takePicture() {   //对外暴露的方法，连续拍照时调用
        if (isTaking) {   //判断是否处于拍照，如果正在拍照，则将请求放入缓存队列
            mWaitAction.add(1);
        } else {
            doTakeAction();
        }}

    /**
     * 拍照方法
     */
    private void doTakeAction() {   //拍照方法
        isTaking = true;
        camera.takePicture(null, null, new Ticpic());
    }


    /**
     * 点击事件
     *
     */

    @OnClick({R.id.opean,R.id.shan,R.id.huan,R.id.show})
    public void onclick(View view){
        switch(view.getId()){
            case R.id.opean://拍照
                takePicture();
                break;
            case R.id.shan://打开闪光灯
                getShan();
                break;
            case R.id.huan://切换摄像头
                onChange();
                break;
            case R.id.show://相册
                //onChange();
                Intent intent = new Intent(this,AlbumsActivity.class);
                // intent.putExtra("url",fileFolder);
                 startActivity(intent);

                break;
        }
    }

    /**
     *   打开闪光灯
     *
     */
    private void getShan() {
        Camera.Parameters parameters = camera.getParameters();
        if(isShan){
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            shan.setText("打开");
            isShan=false;
        }else{
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);

            shan.setText("关闭");
            isShan=true;
        }
        camera.setParameters(parameters);
    }
    /**
     * 连续拍照时进行处理
     */
    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            doTakeAction();
        }
    };
    /**
     * 拍照的处理
     *
     */
    class Ticpic implements Camera.PictureCallback {

        @Override
        public void onPictureTaken(byte[] bytes, Camera camera) {

            if (mWaitAction.size() > 0) {
                mWaitAction.remove(0);   //移除队列中的第一条拍照请求，并执行拍照请求
                mHandler.sendEmptyMessage(0);  //主线程中调用拍照
            } else {  //队列中没有拍照请求，走正常流程
                isTaking = false;
            }
            //new SavePictureTask().execute(data);  //异步保存照片
            //camera.startPreview();  //如果不调用 ，则画面不会更新
            if (bytes.length > 0) {
                try {
                    /**
                     * 给图片添加文字水印
                     *
                     */



                    saveToSDCard(bytes); // 保存图片到sd卡中
                    Toast.makeText(MainActivity.this, "保存成功", Toast.LENGTH_SHORT).show();

                    //显示图片
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    show.setImageBitmap(bitmap);
                   //视图预览实时更新画面
                    camera.cancelAutoFocus(); //这一句很关键
                    //恢复对焦模式
                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                    parameters.setFocusAreas(null);
                    camera.setParameters( parameters);
                    //开启预览
                    camera.startPreview();
                   // Camera.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 将拍下来的照片存放在SD卡中
     * @param data
     * @throws IOException
     */
    public static void saveToSDCard(byte[] data) throws IOException {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss"); // 格式化时间
        String filename = format.format(date) + ".jpg";
         fileFolder = new File(Environment.getExternalStorageDirectory()
                + "/xll/");
        if (!fileFolder.exists()) { // 如果目录不存在，则创建一个名为"finger"的目录
            fileFolder.mkdir();
        }
        File jpgFile = new File(fileFolder, filename);
        FileOutputStream outputStream = new FileOutputStream(jpgFile); // 文件输出流
        outputStream.write(data); // 写入sd卡中
        outputStream.close(); // 关闭输出流
    }


    //创建内部类
    class MyCallSurface implements SurfaceHolder.Callback {

        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            getCameraInfo();//取得摄像头
            RequestPermission();
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
            //得到画面参数
            parameters = camera.getParameters();
            List<Camera.Size> supportedPictureSizes = parameters.getSupportedPictureSizes();
            if (supportedPictureSizes.isEmpty()) {
                parameters.setPreviewSize(i1, i2);
            } else {
                Camera.Size size = supportedPictureSizes.get(0);
                parameters.setPreviewSize(size.width, size.height);
            }
            parameters.setPictureFormat(PixelFormat.JPEG);
            parameters.setPictureSize(i1, i2);

            parameters.setPreviewFrameRate(5);

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            if (camera != null) {
                camera.release();
                camera = null;
            }

        }
    }




    /***
     * 申请权限
     */
    void RequestPermission(){
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.CAMERA},1);
           // Log.d(TAG,"RequestPermission");
        }else{
            initCamera();
        }
    }




    /**
     * 权限回调
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
       // Log.d(TAG,"onRequestPermissionsResult");
        if(requestCode==1){
            if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                initCamera();
                if(camera!=null){
                    camera.startPreview();
                }

            }else{
                Toast.makeText(MainActivity.this,"授权失败",Toast.LENGTH_LONG).show();;
            }
        }
    }

    /**
     * 初始化相机
     */

   private void initCamera(){
        try {
            //打开相机
            camera = Camera.open(iBackCameraIndex);//使用系统硬件打开相机
            bBack=true;
            //设置画面旋转
            camera.setDisplayOrientation(getOrition(MainActivity.this));
            //开启预览
            camera.startPreview();
            camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取摄像头信息
     */

    protected void getCameraInfo(){
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        iCameraCnt = Camera.getNumberOfCameras();

        for (int i = 0; i < iCameraCnt; i++) {
            Camera.getCameraInfo(i,cameraInfo);
            if(cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT){
                iFontCameraIndex = i;
            }
            else if(cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK){
                iBackCameraIndex = i;
            }
        }

    }

/**
 * 点击切换摄像头
 *
 *
 */
public void onChange() {
    if (camera != null) {
        camera.release();
        camera = null;
    }
    if (bBack){
        camera = Camera.open(iFontCameraIndex);
       // Camera.open( Camera.CameraInfo.CAMERA_FACING_FRONT/CAMERA_FACING_BACK);
        try {
            //设置画面旋转
            camera.setDisplayOrientation(getOrition(MainActivity.this));
            //开启预览
            camera.startPreview();
            camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //camera.startPreview();
        bBack = false;
    }
    else{

        camera = Camera.open(iBackCameraIndex);
       // initCamera();
        try {
            //设置画面旋转
            camera.setDisplayOrientation(getOrition(MainActivity.this));
            //开启预览
            camera.startPreview();
            camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        camera.startPreview();
        bBack = true;
    }

}



    /**
     * 判断换面的旋转角度
     */
    public int getOrition(Activity activity) {
        int disgre = 0;
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        switch (rotation) {
            case Surface.ROTATION_0:
                disgre = 90;
                break;
            case Surface.ROTATION_90:
                disgre = 0;
                break;
            case Surface.ROTATION_180:
                disgre = 180;
                break;
            case Surface.ROTATION_270:
                disgre = 270;
                break;
        }

        return disgre;
    }


}
