package info.free.duangjike.friday

import android.app.AlertDialog
import android.app.WallpaperManager
import android.app.WallpaperManager.FLAG_LOCK
import android.app.WallpaperManager.FLAG_SYSTEM
import android.content.Context
import android.content.DialogInterface.BUTTON_POSITIVE
import android.content.Intent
import android.content.Intent.ACTION_SEND
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import info.free.duangjike.R
import info.free.duangjike.ThemeUtil
import info.free.duangjike.Util
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_friday.*
import kotlinx.android.synthetic.main.layout_dialog_input.view.*
import kotlinx.android.synthetic.main.layout_dialog_pick_color.view.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar.*


class FridayActivity : AppCompatActivity() {

    private val shuSongType = 0
    private val kaiType = 1
    private val fangSongType = 2
    private val heiType = 3

    private val bubbleType = 0
    private val bgType = 1

    private val wallType = 0
    private val lockType = 1

    private var white: Int = -1
    private var blue: Int = -1
    private var yellow: Int = -1
    private val today = Calendar.getInstance(Locale.CHINA)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        val flag = WindowManager.LayoutParams.FLAG_FULLSCREEN
        window.setFlags(flag, flag)
        setContentView(R.layout.activity_friday)
        white = resources.getColor(R.color.jikeWhite)
        blue = resources.getColor(R.color.jikeBlue)
        yellow = resources.getColor(R.color.jikeYellow)
        setDate()
        refreshTheme()
        setEvent()
    }

    private fun setEvent() {
        tv_face_type_shu_song?.setOnClickListener {
            switchTypeFace(shuSongType)
        }
        tv_face_type_fang_song?.setOnClickListener {
            switchTypeFace(fangSongType)
        }
        tv_face_type_hei?.setOnClickListener { switchTypeFace(heiType) }
        tv_face_type_kai?.setOnClickListener { switchTypeFace(kaiType) }

        tv_size_square?.setOnClickListener {
            val containerLp = cl_picture_container.layoutParams as ConstraintLayout.LayoutParams
            containerLp.height = 0
            containerLp.dimensionRatio = "1:1"
            cl_picture_container.layoutParams = containerLp
        }
        tv_size_full?.setOnClickListener {
            val containerLp = cl_picture_container.layoutParams as ConstraintLayout.LayoutParams
            containerLp.height = MATCH_PARENT
            containerLp.dimensionRatio = null
            cl_picture_container.layoutParams = containerLp
        }

        tv_set_lock?.setOnClickListener {
            setBitmap(lockType)
        }
        tv_set_wallpaper?.setOnClickListener { setBitmap(wallType) }

        tv_save?.setOnClickListener {
            Flowable.create<Bitmap>({ emitter ->
                val bitmap = Bitmap.createBitmap(cl_picture_container.width, cl_picture_container.height,
                        Bitmap.Config.RGB_565)
                //使用Canvas，调用自定义view控件的onDraw方法，绘制图片
                val canvas = Canvas(bitmap)
                cl_picture_container?.draw(canvas)
                emitter.onNext(bitmap)
                emitter.onComplete()
            }, BackpressureStrategy.BUFFER).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        val format = SimpleDateFormat("yyyy-MM-dd-hh:mm:ss")
                        Util.saveBitmapFile(it, format.format(today.time))
                        Toast.makeText(this, "Ojbk！", LENGTH_SHORT).show()
                    }
        }

        tv_share?.setOnClickListener {
            Flowable.create<Bitmap>({ emitter ->
                val bitmap = Bitmap.createBitmap(cl_picture_container.width, cl_picture_container.height,
                        Bitmap.Config.RGB_565)
                //使用Canvas，调用自定义view控件的onDraw方法，绘制图片
                val canvas = Canvas(bitmap)
                cl_picture_container?.draw(canvas)
                emitter.onNext(bitmap)
                emitter.onComplete()
            }, BackpressureStrategy.BUFFER).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        val format = SimpleDateFormat("yyyy-MM-dd-hh:mm:ss")
                        val shareIntent = Intent(ACTION_SEND)
                        shareIntent.type = "image/*"
                        val uri = Uri.fromFile(Util.saveBitmapFile(it, format.format(today.time)))
                        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
                        this.startActivity(Intent.createChooser(shareIntent, "分享到..."))
                    }

        }

        v_bubble_color_blue?.setOnClickListener { changeBubbleColor(blue) }
        v_bubble_color_white?.setOnClickListener { changeBubbleColor(white) }
        v_bubble_color_yellow?.setOnClickListener { changeBubbleColor(yellow) }
        v_bg_color_blue?.setOnClickListener { changeBgColor(blue) }
        v_bg_color_white?.setOnClickListener { changeBgColor(white) }
        v_bg_color_yellow?.setOnClickListener { changeBgColor(yellow) }

        tv_custom_bubble_color.setOnClickListener { showInputDialog(bubbleType) }
        tv_custom_bg_color.setOnClickListener { showInputDialog(bgType) }
        tv_more_bubble_color.setOnClickListener { showPickColorDialog(bubbleType) }
        tv_more_bg_color.setOnClickListener { showPickColorDialog(bgType) }
    }

    private fun setBitmap(type: Int) {
        try {
            val wpm = getSystemService(Context.WALLPAPER_SERVICE) as WallpaperManager
            Flowable.create<Bitmap>({ emitter ->
                val bitmap = Bitmap.createBitmap(cl_picture_container.width, cl_picture_container.height,
                        Bitmap.Config.RGB_565)
                //使用Canvas，调用自定义view控件的onDraw方法，绘制图片
                val canvas = Canvas(bitmap)
                cl_picture_container?.draw(canvas)
                emitter.onNext(bitmap)
                emitter.onComplete()
            }, BackpressureStrategy.BUFFER).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        wpm.setBitmap(it, null, true, if (type == wallType) FLAG_SYSTEM else FLAG_LOCK or FLAG_SYSTEM)
                        Toast.makeText(this, "Ojbk！", LENGTH_SHORT).show()
                    }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun showInputDialog(type: Int) {
        val inputView = LayoutInflater.from(this).inflate(R.layout.layout_dialog_input, null)

        val titleString = when (type) {
            bubbleType -> "气泡"
            bgType -> "背景"
            else -> "气泡"
        }
        val colorDialog = AlertDialog.Builder(this)
                .setTitle("自定义${titleString}颜色")
                .setView(inputView)
                .setPositiveButton("OK") { _, _ -> }
                .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                .create()
        colorDialog.show()
        colorDialog.getButton(BUTTON_POSITIVE).setOnClickListener {
            val colorString = inputView.et_color_input.text.toString()
            if (colorString.startsWith("#") && (colorString.length == 7 || colorString.length == 7)) {
                when (type) {
                    bubbleType -> changeBubbleColor(Color.parseColor(colorString))
                    bgType -> changeBgColor(Color.parseColor(colorString))
                    else -> changeBubbleColor(Color.parseColor(colorString))
                }
                colorDialog.dismiss()
            } else {
                Toast.makeText(this, "请输入正确的颜色值", LENGTH_SHORT).show()
            }
        }
    }

    private fun refreshTheme() {
        cl_picture_container?.setBackgroundColor(FridayPreference.getBgColor())
        changeBubbleColor(FridayPreference.getBubbleColor())
        switchTypeFace(FridayPreference.getFontType())
        tv_is_friday?.paint?.isFakeBoldText = true
        tv_question?.paint?.isFakeBoldText = true

        tv_face_type_kai.post {
            tv_face_type_kai?.background = ThemeUtil.customShape(blue, blue, 0,
                    tv_face_type_kai.height.toFloat() / 2)
        }
        tv_face_type_hei.post {
            tv_face_type_hei?.background = ThemeUtil.customShape(blue, blue, 0,
                    tv_face_type_hei.height.toFloat() / 2)
        }
        tv_face_type_fang_song.post {
            tv_face_type_fang_song?.background = ThemeUtil.customShape(blue, blue, 0,
                    tv_face_type_fang_song.height.toFloat() / 2)
        }
        tv_face_type_shu_song.post {
            tv_face_type_shu_song?.background = ThemeUtil.customShape(blue, blue, 0,
                    tv_face_type_shu_song.height.toFloat() / 2)
        }
        tv_size_full.post {
            tv_size_full?.background = ThemeUtil.customShape(blue, blue, 0,
                    tv_size_full.height.toFloat() / 2)
        }
        tv_size_square.post {
            tv_size_square?.background = ThemeUtil.customShape(blue, blue, 0,
                    tv_size_square.height.toFloat() / 2)
        }
        tv_set_wallpaper.post {
            tv_set_wallpaper?.background = ThemeUtil.customShape(blue, blue, 0,
                    tv_set_wallpaper.height.toFloat() / 2)
        }
        tv_share.post {
            tv_share?.background = ThemeUtil.customShape(blue, blue, 0,
                    tv_share.height.toFloat() / 2)
        }
        tv_save.post {
            tv_save?.background = ThemeUtil.customShape(blue, blue, 0,
                    tv_save.height.toFloat() / 2)
        }
        tv_set_lock.post {
            tv_set_lock?.background = ThemeUtil.customShape(blue, blue, 0,
                    tv_set_lock.height.toFloat() / 2)
        }
        v_bubble_color_white.background = ThemeUtil.customShape(white, white, 0, ThemeUtil.dip2px(10))
        v_bubble_color_yellow.background = ThemeUtil.customShape(yellow, yellow, 0, ThemeUtil.dip2px(10))
        v_bubble_color_blue.background = ThemeUtil.customShape(blue, blue, 0, ThemeUtil.dip2px(10))
        v_bg_color_white.background = ThemeUtil.customShape(white, white, 0, ThemeUtil.dip2px(10))
        v_bg_color_yellow.background = ThemeUtil.customShape(yellow, yellow, 0, ThemeUtil.dip2px(10))
        v_bg_color_blue.background = ThemeUtil.customShape(blue, blue, 0, ThemeUtil.dip2px(10))
    }

    private fun changeBubbleColor(color: Int) {
        FridayPreference.setBubbleColor(color)
        tv_question?.post {
            tv_question?.background = ThemeUtil.customShape(color, color, 0,
                    tv_question.height.toFloat() / 2)
        }
        tv_triangle.setColor(color)
    }

    private fun changeBgColor(color: Int) {
        FridayPreference.setBgColor(color)
        cl_picture_container.setBackgroundColor(color)
    }

    private fun switchTypeFace(fontType: Int) {
        FridayPreference.setFontType(fontType)
        val typeface = when (fontType) {
            shuSongType -> Typeface.createFromAsset(assets, "FZSSJW.TTF")
            fangSongType -> Typeface.createFromAsset(assets, "FZFSJW.TTF")
            heiType -> Typeface.createFromAsset(assets, "FZHTJW.TTF")
            kaiType -> Typeface.createFromAsset(assets, "FZKTJW.TTF")
            else -> Typeface.createFromAsset(assets, "FZKTJW.TTF")
        }
        tv_is_friday?.typeface = typeface
        tv_question?.typeface = typeface
    }

    private fun setDate() {
        tv_today?.text = "${getWeekDayString(today.get(DAY_OF_WEEK))} ${today.get(YEAR)}" +
                ".${today.get(MONTH) + 1}.${today.get(DAY_OF_MONTH)}"
        if (today.get(DAY_OF_WEEK) == 6) {
            tv_is_friday.text = "是"
        } else {
            tv_is_friday.text = "不是"
        }
    }

    private fun getWeekDayString(day: Int): String {
        return when (day) {
            1 -> "Sunday"
            2 -> "Monday"
            3 -> "Tuesday"
            4 -> "Wednesday"
            5 -> "Thursday"
            6 -> "Friday"
            7 -> "Saturday"
            else -> "Friday"
        }
    }

    private fun showPickColorDialog(type: Int) {
        val titleString = when (type) {
            bubbleType -> "气泡"
            bgType -> "背景"
            else -> "气泡"
        }
        val inputView = LayoutInflater.from(this).inflate(R.layout.layout_dialog_pick_color, null)

        inputView.gv_pick_color.adapter = GridColorAdapter(Util.colorList, this)
        inputView.gv_pick_color.setOnItemClickListener { _, _, position, id ->
            val colorString = Util.colorList[position]
            when (type) {
                bubbleType -> changeBubbleColor(Color.parseColor(colorString))
                bgType -> changeBgColor(Color.parseColor(colorString))
                else -> changeBubbleColor(Color.parseColor(colorString))
            }
        }
        val colorDialog = AlertDialog.Builder(this)
                .setTitle("更多${titleString}颜色")
                .setView(inputView)
                .setPositiveButton("OK") { _, _ -> }
                .setNegativeButton("Cancel") { _, _ ->  }
                .create()
        colorDialog.show()
    }
}
