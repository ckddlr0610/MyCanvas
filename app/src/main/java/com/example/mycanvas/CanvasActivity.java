package com.example.mycanvas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

public class CanvasActivity extends AppCompatActivity {
    GraphicCanvasActivity currentCanvas;
    FrameLayout canvasFrame;
    ImageView imageView;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canvas);

        canvasFrame = findViewById(R.id.canvas_frame);
        imageView = findViewById(R.id.image);
        bottomNavigationView = findViewById(R.id.main_bottom_tab);

        currentCanvas = new GraphicCanvasActivity(this);
        canvasFrame.addView(currentCanvas);

        checkPermission();
        createNewCanvas();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_black :
                        currentCanvas.shapeColor = 0;
                        currentCanvas.invalidate();
                        break;
                    case R.id.nav_red :
                        currentCanvas.shapeColor = 1;
                        currentCanvas.invalidate();
                        break;
                    case R.id.nav_blue :
                        currentCanvas.shapeColor = 2;
                        currentCanvas.invalidate();
                        break;
                    case R.id.nav_green :
                        currentCanvas.shapeColor = 3;
                        currentCanvas.invalidate();
                        break;
                }
                return true;
            }
        });
    }

    private void createNewCanvas() {
        currentCanvas.shapes.clear();
        imageView.setImageBitmap(null);
        currentCanvas.createNewCanvas();
        currentCanvas.invalidate();
        createToast("새로운 화면을 만들었습니다.");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        SubMenu subMenu1 = menu.addSubMenu("파일");
        subMenu1.add(0, 1, 0, "새로 만들기");
        subMenu1.add(0, 2, 0, "그림 저장");
        subMenu1.add(0, 3, 0, "그림 불러오기");

        SubMenu subMenu = menu.addSubMenu("도구");
        subMenu.add(1, 4, 0, "연필");
        subMenu.add(1, 5, 0, "선");
        subMenu.add(1, 6, 0, "사각형");
        subMenu.add(1, 7, 0, "원");
        subMenu.add(1, 8, 0, "지우개");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                createNewCanvas();
                return true;
            case 2:
                savePicture();
                return true;
            case 3:
                loadPicture();
                return true;
            case 4:
                currentCanvas.toolState = "pencil";
                createToast("연필을 선택했습니다.");
                return true;
            case 5:
                currentCanvas.toolState = "line";
                createToast("선을 선택했습니다.");
                return true;
            case 6:
                currentCanvas.toolState = "rect";
                createToast("사각형을 선택했습니다.");
                return true;
            case 7:
                currentCanvas.toolState = "circle";
                createToast("원을 선택했습니다.");
                return true;
            case 8:
                currentCanvas.toolState = "eraser";
                createToast("지우개를 선택했습니다.");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadPicture() {
        currentCanvas.shapes.clear();
        currentCanvas.invalidate();
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        Bitmap bm = BitmapFactory.decodeFile(dir+"/my.png").copy(Bitmap.Config.ARGB_8888, true);
        Log.d("phoro",dir+"/my.png");
        createToast("파일을 불러왔습니다.");
        imageView.setImageBitmap(bm);
        currentCanvas.draw(new Canvas(bm));
        //currentCanvas.invalidate();
    }

    private void savePicture() {
        currentCanvas.invalidate();
        currentCanvas.setDrawingCacheEnabled(true);
        Bitmap screenshot = Bitmap.createBitmap(currentCanvas.getDrawingCache());
        currentCanvas.setDrawingCacheEnabled(false);   // 캐쉬닫기
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if(!dir.exists()) dir.mkdirs();

        FileOutputStream fos;
        try {
            fos = new FileOutputStream(new File(dir, "my.png"));
            screenshot.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            createToast("저장했습니다.");
        } catch (Exception e) {
            Log.e("phoro","그림저장오류",e);
            createToast("저장 실패했습니다.");
        }
    }

    private void createToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public void checkPermission(){
        if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }
}
