package com.example.vinylstudio;

import android.app.Activity;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

public class MainActivity extends Activity {

    VinylView vinyl;
    LinearLayout controls;

    int dp(float n) {
        return (int) (n * getResources().getDisplayMetrics().density + 0.5f);
    }

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        build();
    }

    TextView text(String s, float size) {
        TextView t = new TextView(this);
        t.setText(s);
        t.setTextColor(Color.WHITE);
        t.setTextSize(size);
        t.setGravity(Gravity.CENTER);
        return t;
    }

    Button pill(String s) {
        Button b = new Button(this);
        b.setText(s);
        b.setTextColor(Color.WHITE);
        b.setTextSize(14);
        b.setAllCaps(false);
        b.setBackground(round(0xFF3A2A35, 24));
        return b;
    }

    GradientDrawable round(int c, int r) {
        GradientDrawable g = new GradientDrawable();
        g.setColor(c);
        g.setCornerRadius(dp(r));
        return g;
    }

    void build() {

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(0xFF160C13);

        LinearLayout top = new LinearLayout(this);
        top.setPadding(dp(14), dp(10), dp(14), dp(4));
        top.setGravity(Gravity.CENTER_VERTICAL);

        TextView title = text("VINYL STUDIO", 18);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);

        top.addView(
                title,
                new LinearLayout.LayoutParams(0, dp(46), 1)
        );

        Button choose = pill("Choose image");

        top.addView(
                choose,
                new LinearLayout.LayoutParams(dp(126), dp(44))
        );

        choose.setOnClickListener(v -> pickImage());

        root.addView(top);

        vinyl = new VinylView(this);

        root.addView(
                vinyl,
                new LinearLayout.LayoutParams(-1, dp(390))
        );

        LinearLayout actions = new LinearLayout(this);
        actions.setPadding(dp(14), dp(4), dp(14), dp(4));

        Button base = pill("Base");
        Button effects = pill("Effects");
        Button art = pill("Artwork");
        Button export = pill("Export PNG");

        actions.addView(
                base,
                new LinearLayout.LayoutParams(0, dp(46), 1)
        );

        actions.addView(
                effects,
                new LinearLayout.LayoutParams(0, dp(46), 1)
        );

        actions.addView(
                art,
                new LinearLayout.LayoutParams(0, dp(46), 1)
        );

        actions.addView(
                export,
                new LinearLayout.LayoutParams(0, dp(46), 1)
        );

        root.addView(actions);

        controls = new LinearLayout(this);
        controls.setOrientation(LinearLayout.VERTICAL);
        controls.setPadding(
                dp(18),
                dp(8),
                dp(18),
                dp(18)
        );

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.addView(controls);

        root.addView(
                scroll,
                new LinearLayout.LayoutParams(-1, 0, 1)
        );

        base.setOnClickListener(v -> showBase());
        effects.setOnClickListener(v -> showEffects());
        art.setOnClickListener(v -> showArtwork());
        export.setOnClickListener(v -> createExportDocument());

        showBase();

        setContentView(root);
    }

    void clear() {
        controls.removeAllViews();
    }

    void heading(String s) {

        TextView t = text(s, 17);

        t.setGravity(
                Gravity.LEFT | Gravity.CENTER_VERTICAL
        );

        t.setPadding(
                dp(2),
                dp(8),
                0,
                dp(2)
        );

        t.setTypeface(null, Typeface.BOLD);

        controls.addView(
                t,
                new LinearLayout.LayoutParams(-1, dp(44))
        );
    }

    void showBase() {

        clear();

        heading("Vinyl colour");

        LinearLayout row = new LinearLayout(this);
        row.setGravity(Gravity.CENTER);

        int[] colors = {
                0xFF111111,
                0xFFF4F4F4,
                0xFFE91E63,
                0xFF7B1FA2,
                0xFF1565C0,
                0xFFFF9800,
                0xFF2E7D32
        };

        for (int c : colors) {

            TextView swatch = text("", 1);

            swatch.setBackground(
                    round(c, 50)
            );

            LinearLayout.LayoutParams p =
                    new LinearLayout.LayoutParams(
                            dp(42),
                            dp(42)
                    );

            p.setMargins(
                    dp(4),
                    0,
                    dp(4),
                    dp(8)
            );

            row.addView(swatch, p);

            swatch.setOnClickListener(v -> {
                vinyl.vinylColor = c;
                vinyl.invalidate();
            });
        }

        controls.addView(row);

        heading("Label style");

        LinearLayout labels = new LinearLayout(this);

        String[] labelNames = {
                "Dark",
                "Red",
                "Green",
                "Cream"
        };

        for (int i = 0; i < 4; i++) {

            final int index = i;

            Button b = pill(labelNames[i]);

            labels.addView(
                    b,
                    new LinearLayout.LayoutParams(
                            0,
                            dp(46),
                            1
                    )
            );

            b.setOnClickListener(v -> {
                vinyl.label = index;
                vinyl.invalidate();
            });
        }

        controls.addView(labels);

        heading("Shape");

        LinearLayout shapes = new LinearLayout(this);

        String[] shapeNames = {
                "LP",
                "Rounded",
                "Heart",
                "Star"
        };

        for (int i = 0; i < 4; i++) {

            final int index = i;

            Button b = pill(shapeNames[i]);

            shapes.addView(
                    b,
                    new LinearLayout.LayoutParams(
                            0,
                            dp(46),
                            1
                    )
            );

            b.setOnClickListener(v -> {
                vinyl.shape = index;
                vinyl.invalidate();
            });
        }

        controls.addView(shapes);

        heading("Centre label size");

        addSeek(
                "Size",
                18,
                42,
                vinyl.labelSize,
                v -> {
                    vinyl.labelSize = v;
                    vinyl.invalidate();
                }
        );
    }

    void showEffects() {

        clear();

        heading("Vinyl effects");

        addSeek(
                "Gloss",
                0,
                100,
                vinyl.gloss,
                v -> {
                    vinyl.gloss = v;
                    vinyl.invalidate();
                }
        );

        addSeek(
                "Grooves",
                0,
                100,
                vinyl.groove,
                v -> {
                    vinyl.groove = v;
                    vinyl.invalidate();
                }
        );

        addSeek(
                "Grain",
                0,
                100,
                vinyl.grain,
                v -> {
                    vinyl.grain = v;
                    vinyl.invalidate();
                }
        );

        addSeek(
                "Highlight",
                0,
                100,
                vinyl.highlight,
                v -> {
                    vinyl.highlight = v;
                    vinyl.invalidate();
                }
        );

        addSeek(
                "Transparency",
                0,
                75,
                vinyl.transparency,
                v -> {
                    vinyl.transparency = v;
                    vinyl.invalidate();
                }
        );

        heading("Preview");

        Button spin = pill(
                vinyl.spinning
                        ? "Pause spin"
                        : "Spin record"
        );

        controls.addView(
                spin,
                new LinearLayout.LayoutParams(
                        -1,
                        dp(48)
                )
        );

        spin.setOnClickListener(v -> {

            vinyl.spinning = !vinyl.spinning;

            spin.setText(
                    vinyl.spinning
                            ? "Pause spin"
                            : "Spin record"
            );

            vinyl.invalidate();
        });
    }

    void showArtwork() {

        clear();

        heading("Album artwork");

        TextView hint = text(
                vinyl.cover == null
                        ? "Choose an image first."
                        : "Adjust how the image sits on the sleeve and centre label.",
                14
        );

        hint.setTextColor(0xFFCCBFC7);
        hint.setGravity(Gravity.LEFT);

        controls.addView(
                hint,
                new LinearLayout.LayoutParams(
                        -1,
                        dp(42)
                )
        );

        addSeek(
                "Crop zoom",
                100,
                220,
                vinyl.artZoom,
                v -> {
                    vinyl.artZoom = v;
                    vinyl.invalidate();
                }
        );

        addSeek(
                "Horizontal",
                -100,
                100,
                vinyl.artX,
                v -> {
                    vinyl.artX = v;
                    vinyl.invalidate();
                }
        );

        addSeek(
                "Vertical",
                -100,
                100,
                vinyl.artY,
                v -> {
                    vinyl.artY = v;
                    vinyl.invalidate();
                }
        );

        heading("Centre label artwork");

        Button toggle = pill(
                vinyl.useArtworkOnLabel
                        ? "Artwork label: ON"
                        : "Artwork label: OFF"
        );

        controls.addView(
                toggle,
                new LinearLayout.LayoutParams(
                        -1,
                        dp(48)
                )
        );

        toggle.setOnClickListener(v -> {

            vinyl.useArtworkOnLabel =
                    !vinyl.useArtworkOnLabel;

            toggle.setText(
                    vinyl.useArtworkOnLabel
                            ? "Artwork label: ON"
                            : "Artwork label: OFF"
            );

            vinyl.invalidate();
        });
    }

    interface IntSetter {
        void set(int value);
    }

    void addSeek(
            String name,
            int min,
            int max,
            int value,
            IntSetter setter
    ) {

        TextView label = text(
                name + " " + value,
                14
        );

        label.setGravity(Gravity.LEFT);

        controls.addView(
                label,
                new LinearLayout.LayoutParams(
                        -1,
                        dp(30)
                )
        );

        SeekBar seek = new SeekBar(this);

        seek.setMax(max - min);
        seek.setProgress(value - min);

        controls.addView(
                seek,
                new LinearLayout.LayoutParams(
                        -1,
                        dp(40)
                )
        );

        seek.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(
                            SeekBar bar,
                            int progress,
                            boolean fromUser
                    ) {

                        int v = progress + min;

                        label.setText(
                                name + " " + v
                        );

                        setter.set(v);
                    }

                    @Override
                    public void onStartTrackingTouch(
                            SeekBar bar
                    ) {
                    }

                    @Override
                    public void onStopTrackingTouch(
                            SeekBar bar
                    ) {
                    }
                }
        );
    }

    void pickImage() {

        Intent intent =
                new Intent(Intent.ACTION_OPEN_DOCUMENT);

        intent.setType("image/*");

        intent.addCategory(
                Intent.CATEGORY_OPENABLE
        );

        startActivityForResult(intent, 42);
    }

    void createExportDocument() {

        Intent intent =
                new Intent(Intent.ACTION_CREATE_DOCUMENT);

        intent.addCategory(
                Intent.CATEGORY_OPENABLE
        );

        intent.setType("image/png");

        intent.putExtra(
                Intent.EXTRA_TITLE,
                "vinyl-studio.png"
        );

        startActivityForResult(intent, 77);
    }

    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data
    ) {

        super.onActivityResult(
                requestCode,
                resultCode,
                data
        );

        if (resultCode != RESULT_OK || data == null) {
            return;
        }

        if (requestCode == 42) {

            try {

                Uri uri = data.getData();

                final int flags =
                        data.getFlags()
                                & Intent.FLAG_GRANT_READ_URI_PERMISSION;

                try {
                    getContentResolver()
                            .takePersistableUriPermission(
                                    uri,
                                    flags
                            );
                } catch (Exception ignored) {
                }

                InputStream input =
                        getContentResolver()
                                .openInputStream(uri);

                Bitmap bitmap =
                        BitmapFactory.decodeStream(input);

                if (input != null) {
                    input.close();
                }

                vinyl.cover = bitmap;

                vinyl.invalidate();

                showArtwork();

            } catch (Exception e) {

                Toast.makeText(
                        this,
                        "Couldn't load image",
                        Toast.LENGTH_SHORT
                ).show();
            }

        } else if (requestCode == 77) {

            try {

                Uri uri = data.getData();

                Bitmap output =
                        vinyl.renderExport(1600);

                OutputStream outputStream =
                        getContentResolver()
                                .openOutputStream(uri);

                if (outputStream != null) {

                    output.compress(
                            Bitmap.CompressFormat.PNG,
                            100,
                            outputStream
                    );

                    outputStream.flush();
                    outputStream.close();
                }

                Toast.makeText(
                        this,
                        "PNG exported",
                        Toast.LENGTH_SHORT
                ).show();

            } catch (Exception e) {

                Toast.makeText(
                        this,
                        "Export failed",
                        Toast.LENGTH_SHORT
                ).show();
            }
        }
    }

    public class VinylView extends View {

        Paint p = new Paint(
                Paint.ANTI_ALIAS_FLAG
                        | Paint.FILTER_BITMAP_FLAG
        );

        Bitmap cover;

        int vinylColor = 0xFF111111;
        int label = 0;
        int shape = 0;

        int gloss = 72;
        int groove = 70;
        int grain = 10;
        int highlight = 68;
        int transparency = 0;
        int labelSize = 29;

        int artZoom = 120;
        int artX = 0;
        int artY = 0;

        boolean useArtworkOnLabel = true;
        boolean spinning = false;

        float angle = 0;

        Random rng = new Random(12);

        VinylView(Context context) {
            super(context);
            setLayerType(
                    LAYER_TYPE_SOFTWARE,
                    null
            );
        }

        @Override
        protected void onDraw(Canvas canvas) {

            super.onDraw(canvas);

            drawScene(
                    canvas,
                    getWidth(),
                    getHeight(),
                    false
            );

            if (spinning) {

                angle += 1.35f;

                postInvalidateDelayed(16);
            }
        }

        Bitmap renderExport(int size) {

            Bitmap bitmap =
                    Bitmap.createBitmap(
                            size,
                            size,
                            Bitmap.Config.ARGB_8888
                    );

            Canvas canvas =
                    new Canvas(bitmap);

            drawScene(
                    canvas,
                    size,
                    size,
                    true
            );

            return bitmap;
        }

        void drawScene(
                Canvas canvas,
                float w,
                float h,
                boolean export
        ) {

            p.setStyle(Paint.Style.FILL);

            p.setShader(
                    new LinearGradient(
                            0,
                            0,
                            w,
                            h,
                            0xFF130B11,
                            0xFF2B1021,
                            Shader.TileMode.CLAMP
                    )
            );

            canvas.drawRect(
                    0,
                    0,
                    w,
                    h,
                    p
            );

            p.setShader(null);

            float cx =
                    w * (export ? 0.61f : 0.67f);

            float cy = h * 0.49f;

            float r =
                    Math.min(w, h)
                            * (export ? 0.28f : 0.34f);

            float sleeveSize =
                    Math.min(w, h)
                            * (export ? 0.50f : 0.36f);

            float sx =
                    w * (export ? 0.07f : 0.06f);

            float sy =
                    h * (export ? 0.23f : 0.13f);

            if (cover != null) {

                p.setShadowLayer(
                        Math.max(8, w * 0.015f),
                        0,
                        w * 0.012f,
                        0x99000000
                );

                drawCoverCrop(
                        canvas,
                        cover,
                        new RectF(
                                sx,
                                sy,
                                sx + sleeveSize,
                                sy + sleeveSize
                        ),
                        artZoom,
                        artX,
                        artY,
                        p
                );

                p.clearShadowLayer();

            } else {

                p.setColor(0xFF392A35);

                canvas.drawRoundRect(
                        new RectF(
                                sx,
                                sy,
                                sx + sleeveSize,
                                sy + sleeveSize
                        ),
                        w * 0.015f,
                        w * 0.015f,
                        p
                );

                p.setColor(0xFF9E8B98);
                p.setTextAlign(Paint.Align.CENTER);
                p.setTextSize(sleeveSize * 0.055f);

                canvas.drawText(
                        "CHOOSE ARTWORK",
                        sx + sleeveSize / 2,
                        sy + sleeveSize / 2,
                        p
                );
            }

            canvas.save();

            canvas.rotate(
                    angle,
                    cx,
                    cy
            );

            drawRecord(
                    canvas,
                    cx,
                    cy,
                    r
            );

            canvas.restore();
        }

        void drawRecord(
                Canvas canvas,
                float x,
                float y,
                float r
        ) {

            p.setStyle(Paint.Style.FILL);

            int alpha =
                    255 - (transparency * 255 / 100);

            p.setColor(
                    (alpha << 24)
                            | (vinylColor & 0x00FFFFFF)
            );

            Path shapePath =
                    recordPath(x, y, r);

            p.setShadowLayer(
                    r * 0.055f,
                    0,
                    r * 0.04f,
                    0x99000000
            );

            canvas.drawPath(
                    shapePath,
                    p
            );

            p.clearShadowLayer();

            if (groove > 0) {

                p.setStyle(Paint.Style.STROKE);

                int rings =
                        12 + (groove * 22 / 100);

                for (int i = 0; i < rings; i++) {

                    float rr =
                            r * (
                                    0.96f
                                            - i * (0.70f / rings)
                            );

                    int a =
                            12 + (groove * 40 / 100);

                    p.setColor(
                            (a << 24)
                                    | 0x00FFFFFF
                    );

                    p.setStrokeWidth(
                            Math.max(
                                    1,
                                    r * 0.0035f
                            )
                    );

                    canvas.drawCircle(
                            x,
                            y,
                            rr,
                            p
                    );
                }
            }

            if (gloss > 0) {

                p.setStyle(Paint.Style.FILL);

                int a =
                        gloss * 95 / 100;

                p.setShader(
                        new RadialGradient(
                                x - r * 0.25f,
                                y - r * 0.35f,
                                r * 1.35f,
                                new int[]{
                                        (a << 24)
                                                | 0x00FFFFFF,
                                        0x00FFFFFF,
                                        0x33000000
                                },
                                new float[]{
                                        0f,
                                        0.55f,
                                        1f
                                },
                                Shader.TileMode.CLAMP
                        )
                );

                canvas.drawPath(
                        shapePath,
                        p
                );

                p.setShader(null);
            }

            if (highlight > 0) {

                p.setStyle(Paint.Style.STROKE);

                p.setStrokeWidth(
                        r * 0.11f
                );

                p.setStrokeCap(
                        Paint.Cap.ROUND
                );

                p.setColor(
                        ((highlight * 90 / 100) << 24)
                                | 0x00FFFFFF
                );

                RectF arc =
                        new RectF(
                                x - r * 0.78f,
                                y - r * 0.78f,
                                x + r * 0.78f,
                                y + r * 0.78f
                        );

                canvas.drawArc(
                        arc,
                        205,
                        65,
                        false,
                        p
                );

                p.setStrokeCap(
                        Paint.Cap.BUTT
                );
            }

            if (grain > 0) {

                p.setStyle(Paint.Style.FILL);

                rng.setSeed(22);

                int dots =
                        grain * 3;

                for (int i = 0; i < dots; i++) {

                    double randomAngle =
                            rng.nextDouble()
                                    * Math.PI
                                    * 2;

                    float rr =
                            (float) Math.sqrt(
                                    rng.nextDouble()
                            ) * r * 0.95f;

                    float gx =
                            x
                                    + (float) Math.cos(randomAngle)
                                    * rr;

                    float gy =
                            y
                                    + (float) Math.sin(randomAngle)
                                    * rr;

                    p.setColor(
                            0x22FFFFFF
                    );

                    canvas.drawCircle(
                            gx,
                            gy,
                            Math.max(
                                    1,
                                    r * 0.003f
                            ),
                            p
                    );
                }
            }

            float lr =
                    r * (labelSize / 100f);

            p.setStyle(Paint.Style.FILL);

            if (useArtworkOnLabel && cover != null) {

                Path clip = new Path();

                clip.addCircle(
                        x,
                        y,
                        lr,
                        Path.Direction.CW
                );

                canvas.save();

                canvas.clipPath(clip);

                drawCoverCrop(
                        canvas,
                        cover,
                        new RectF(
                                x - lr,
                                y - lr,
                                x + lr,
                                y + lr
                        ),
                        artZoom,
                        artX,
                        artY,
                        p
                );

                canvas.restore();

                p.setColor(0x33000000);

                canvas.drawCircle(
                        x,
                        y,
                        lr,
                        p
                );

            } else {

                p.setColor(
                        labelColor()
                );

                canvas.drawCircle(
                        x,
                        y,
                        lr,
                        p
                );
            }

            p.setStyle(Paint.Style.STROKE);

            p.setStrokeWidth(
                    Math.max(
                            1,
                            r * 0.007f
                    )
            );

            p.setColor(0x99FFFFFF);

            canvas.drawCircle(
                    x,
                    y,
                    lr * 0.93f,
                    p
            );

            p.setStyle(Paint.Style.FILL);

            p.setColor(0xFF080808);

            canvas.drawCircle(
                    x,
                    y,
                    r * 0.038f,
                    p
            );

            p.setTextAlign(
                    Paint.Align.CENTER
            );

            p.setColor(Color.WHITE);

            p.setTypeface(
                    Typeface.DEFAULT_BOLD
            );

            p.setTextSize(
                    r * 0.072f
            );

            canvas.drawText(
                    "VINYL",
                    x,
                    y - lr * 0.12f,
                    p
            );

            p.setTypeface(
                    Typeface.DEFAULT
            );

            p.setTextSize(
                    r * 0.045f
            );

            canvas.drawText(
                    "STUDIO",
                    x,
                    y + lr * 0.20f,
                    p
            );
        }

        Path recordPath(
                float x,
                float y,
                float r
        ) {

            Path path = new Path();

            if (shape == 0) {

                path.addCircle(
                        x,
                        y,
                        r,
                        Path.Direction.CW
                );

            } else if (shape == 1) {

                path.addRoundRect(
                        new RectF(
                                x - r,
                                y - r,
                                x + r,
                                y + r
                        ),
                        r * 0.22f,
                        r * 0.22f,
                        Path.Direction.CW
                );

            } else if (shape == 2) {

                path.moveTo(
                        x,
                        y + r * 0.82f
                );

                path.cubicTo(
                        x - r * 1.14f,
                        y + r * 0.02f,
                        x - r * 0.68f,
                        y - r * 0.82f,
                        x,
                        y - r * 0.28f
                );

                path.cubicTo(
                        x + r * 0.68f,
                        y - r * 0.82f,
                        x + r * 1.14f,
                        y + r * 0.02f,
                        x,
                        y + r * 0.82f
                );

                path.close();

            } else {

                for (int i = 0; i < 10; i++) {

                    double a =
                            -Math.PI / 2
                                    + i * Math.PI / 5;

                    float rr =
                            (i % 2 == 0)
                                    ? r
                                    : r * 0.48f;

                    float px =
                            x
                                    + (float) Math.cos(a)
                                    * rr;

                    float py =
                            y
                                    + (float) Math.sin(a)
                                    * rr;

                    if (i == 0) {
                        path.moveTo(px, py);
                    } else {
                        path.lineTo(px, py);
                    }
                }

                path.close();
            }

            return path;
        }

        int labelColor() {

            switch (label) {

                case 1:
                    return 0xFFB82B35;

                case 2:
                    return 0xFF356D55;

                case 3:
                    return 0xFFE8D8BC;

                default:
                    return 0xFF1B181C;
            }
        }

        /*
         * FIX:
         *
         * Canvas.drawBitmap() requires:
         *
         * drawBitmap(Bitmap, Rect, RectF, Paint)
         *
         * The source rectangle therefore has to be an integer Rect,
         * not a RectF.
         */
        void drawCoverCrop(
                Canvas canvas,
                Bitmap bitmap,
                RectF destination,
                int zoom,
                int offsetX,
                int offsetY,
                Paint paint
        ) {

            float target =
                    destination.width();

            float scale =
                    Math.max(
                            target / bitmap.getWidth(),
                            target / bitmap.getHeight()
                    ) * (zoom / 100f);

            float sourceWidth =
                    destination.width() / scale;

            float sourceHeight =
                    destination.height() / scale;

            float maxX =
                    Math.max(
                            0,
                            bitmap.getWidth()
                                    - sourceWidth
                    );

            float maxY =
                    Math.max(
                            0,
                            bitmap.getHeight()
                                    - sourceHeight
                    );

            float centerX =
                    bitmap.getWidth() / 2f
                            + (offsetX / 100f)
                            * maxX / 2f;

            float centerY =
                    bitmap.getHeight() / 2f
                            + (offsetY / 100f)
                            * maxY / 2f;

            RectF sourceFloat =
                    new RectF(
                            centerX - sourceWidth / 2f,
                            centerY - sourceHeight / 2f,
                            centerX + sourceWidth / 2f,
                            centerY + sourceHeight / 2f
                    );

            if (sourceFloat.left < 0) {
                sourceFloat.offset(
                        -sourceFloat.left,
                        0
                );
            }

            if (sourceFloat.top < 0) {
                sourceFloat.offset(
                        0,
                        -sourceFloat.top
                );
            }

            if (sourceFloat.right > bitmap.getWidth()) {
                sourceFloat.offset(
                        bitmap.getWidth()
                                - sourceFloat.right,
                        0
                );
            }

            if (sourceFloat.bottom > bitmap.getHeight()) {
                sourceFloat.offset(
                        0,
                        bitmap.getHeight()
                                - sourceFloat.bottom
                );
            }

            /*
             * Convert RectF -> Rect.
             * This is the important compile fix.
             */
            Rect source =
                    new Rect(
                            Math.round(sourceFloat.left),
                            Math.round(sourceFloat.top),
                            Math.round(sourceFloat.right),
                            Math.round(sourceFloat.bottom)
                    );

            canvas.drawBitmap(
                    bitmap,
                    source,
                    destination,
                    paint
            );
        }
    }
}
