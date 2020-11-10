package cn.yanstu.displaycontrol;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.util.Log;

import java.util.Arrays;

public class MyTileService extends TileService {

    final String LOG_TAG = "MyTileService";

    /**
     * 当用户从Edit栏添加到快速设置中调用
     */
    @Override
    public void onTileAdded() {
        permission();
        updateLabel(getCurrMin());
    }

    /**
     * 点击的时候
     */
    @Override
    public void onClick() {
        int[] mins = {1, 2, 5, 10, 30};
        int currMin = getCurrMin();
        for (int index = 0; index < mins.length; index++) {
            if (currMin >= 30) {
                setCurrMin(mins[0]);
                break;
            }
            if (mins[index] == currMin) {
                setCurrMin(mins[index + 1]);
                break;
            }
        }
    }

    /**
     * 获取当前的息屏时间分钟
     *
     * @return 分钟
     */
    private int getCurrMin() {
        int currMin = 999;
        try {
            currMin = Settings.System.getInt(getContentResolver(), "screen_off_timeout") / 1000 / 60;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return currMin;
    }

    /**
     * 修改min为当前息屏时间
     *
     * @param min 分钟
     */
    private void setCurrMin(int min) {
        Settings.System.putInt(getContentResolver(), "screen_off_timeout", 1000 * 60 * min);
        updateLabel(min);
    }

    /**
     * 更新磁贴当前息屏时间
     *
     * @param min 分钟
     */
    public void updateLabel(int min) {
        this.getQsTile().setLabel(min + "分钟");
        //更新Tile
        getQsTile().updateTile();
    }

    /**
     * 权限检查
     */
    private void permission() {
        //要确保API Level 大于等于 25才可以创建动态shortcut，否则会报异常。
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            initDynamicShortcuts();
        }
        Context context = getApplicationContext();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //修改系统设置权限检查
            if (!Settings.System.canWrite(context)) {
                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + context.getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        }
    }

    /**
     * 初始化Shortcuts
     */
    private void initDynamicShortcuts() {
        //①、创建动态快捷方式的第一步，创建ShortcutManager
        ShortcutManager scManager = getSystemService(ShortcutManager.class);
        //②、构建动态快捷方式的详细信息
        ShortcutInfo scInfo1 = new ShortcutInfo.Builder(this, "dynamic1")
                .setShortLabel("1分钟")
                .setLongLabel("1分钟")
                .setIcon(Icon.createWithResource(this, R.drawable.ic_launcher))
                .setIntents(new Intent[]{
                        new Intent(Intent.ACTION_MAIN, Uri.EMPTY, this, MainActivity.class)
                                .putExtra("time", 1),
                })
                .build();
        ShortcutInfo scInfo2 = new ShortcutInfo.Builder(this, "dynamic2")
                .setShortLabel("2分钟")
                .setLongLabel("2分钟")
                .setIcon(Icon.createWithResource(this, R.drawable.ic_launcher))
                .setIntents(new Intent[]{
                        new Intent(Intent.ACTION_MAIN, Uri.EMPTY, this, MainActivity.class)
                                .putExtra("time", 2),})
                .build();
        ShortcutInfo scInfo10 = new ShortcutInfo.Builder(this, "dynamic10")
                .setShortLabel("10分钟")
                .setLongLabel("10分钟")
                .setIcon(Icon.createWithResource(this, R.drawable.ic_launcher))
                .setIntents(new Intent[]{
                        new Intent(Intent.ACTION_MAIN, Uri.EMPTY, this, MainActivity.class)
                                .putExtra("time", 10),})
                .build();
        ShortcutInfo scInfo30 = new ShortcutInfo.Builder(this, "dynamic30")
                .setShortLabel("30分钟")
                .setLongLabel("30分钟")
                .setIcon(Icon.createWithResource(this, R.drawable.ic_launcher))
                .setIntents(new Intent[]{
                        new Intent(Intent.ACTION_MAIN, Uri.EMPTY, this, MainActivity.class)
                                .putExtra("time", 30),})
                .build();
        //③、为ShortcutManager设置动态快捷方式集合
        scManager.setDynamicShortcuts(Arrays.asList(scInfo1, scInfo2, scInfo10, scInfo30));
        //④、更新快捷方式集合
        scManager.updateShortcuts(Arrays.asList(scInfo1, scInfo2, scInfo10, scInfo30));
    }

}