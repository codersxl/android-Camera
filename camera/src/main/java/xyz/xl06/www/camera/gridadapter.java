package xyz.xl06.www.camera;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;

public class gridadapter extends BaseAdapter {

    private  File[] file;
    private int[]  resIds;
    private String[] titles;
    private Context context;
    private LayoutInflater inflater;


    public gridadapter(AlbumsActivity context, File[] files) {
        super();
        this.resIds = resIds;
        this.titles = titles;
        this.context = context;
        this.file=files;
        this.inflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return file.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return file[position];
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        // TODO Auto-generated method stub

        v = inflater.inflate(R.layout.more_grid_item, null);
        ImageView img=    (ImageView) v.findViewById(R.id.more_grid_item_img);
        TextView tv=  (TextView) v.findViewById(R.id.more_grid_item_tv);
        String name = file[position].getName();

        File  fileFolder = new File(Environment.getExternalStorageDirectory()
                + "/xll/");
        File jpgFile = new File(fileFolder, name);
        Uri filepath = Uri.fromFile(jpgFile);
        // Bitmap bitmap = BitmapFactory.decodeFile(filepath.getPath());
       // Bitmap decodeBitmapFromFile = getDecodeBitmapFromFile(filepath.getPath(), 40, 40);
        // runOnUiThread()

        //img.setImageBitmap(decodeBitmapFromFile);
        Glide.with(context).load(filepath).into(img);
        tv.setText(this.file[position].getName());
        return v;
    }

    /**
     * 图片压缩
     */


    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // 源图片的高度和宽度
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            // 计算出实际宽高和目标宽高的比率
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            // 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
            // 一定都会大于等于目标的宽和高。
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    public static Bitmap getDecodeBitmapFromFile(String fileName,
                                                 int reqWidth, int reqHeight) {
        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(fileName, options);
        // 调用上面定义的方法计算inSampleSize值
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(fileName, options);
    }

    public void setdatas(File[] setdata) {
        this.file=setdata;
        notifyDataSetChanged();
    }


}
