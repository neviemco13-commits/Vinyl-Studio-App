package com.example.vinylstudio;

import android.app.*;
import android.os.Bundle;
import android.content.*;
import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.view.*;
import android.widget.*;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

public class MainActivity extends Activity {
    VinylView vinyl;
    LinearLayout controls;
    int dp(float n){ return (int)(n*getResources().getDisplayMetrics().density+0.5f); }

    @Override public void onCreate(Bundle b){ super.onCreate(b); build(); }

    TextView text(String s,float size){ TextView t=new TextView(this); t.setText(s); t.setTextColor(Color.WHITE); t.setTextSize(size); t.setGravity(Gravity.CENTER); return t; }
    Button pill(String s){ Button b=new Button(this); b.setText(s); b.setTextColor(Color.WHITE); b.setTextSize(14); b.setAllCaps(false); b.setBackground(round(0xFF3A2A35,24)); return b; }
    GradientDrawable round(int c,int r){ GradientDrawable g=new GradientDrawable(); g.setColor(c); g.setCornerRadius(dp(r)); return g; }

    void build(){
        LinearLayout root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setBackgroundColor(0xFF160C13);

        LinearLayout top=new LinearLayout(this); top.setPadding(dp(14),dp(10),dp(14),dp(4)); top.setGravity(Gravity.CENTER_VERTICAL);
        TextView title=text("VINYL STUDIO",18); title.setTypeface(null,1); title.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
        top.addView(title,new LinearLayout.LayoutParams(0,dp(46),1));
        Button choose=pill("Choose image"); top.addView(choose,new LinearLayout.LayoutParams(dp(126),dp(44))); choose.setOnClickListener(v->pickImage());
        root.addView(top);

        vinyl=new VinylView(this); root.addView(vinyl,new LinearLayout.LayoutParams(-1,dp(390)));

        LinearLayout actions=new LinearLayout(this); actions.setPadding(dp(14),dp(4),dp(14),dp(4));
        Button base=pill("Base"), effects=pill("Effects"), art=pill("Artwork"), export=pill("Export PNG");
        actions.addView(base,new LinearLayout.LayoutParams(0,dp(46),1));
        actions.addView(effects,new LinearLayout.LayoutParams(0,dp(46),1));
        actions.addView(art,new LinearLayout.LayoutParams(0,dp(46),1));
        actions.addView(export,new LinearLayout.LayoutParams(0,dp(46),1));
        root.addView(actions);

        controls=new LinearLayout(this); controls.setOrientation(LinearLayout.VERTICAL); controls.setPadding(dp(18),dp(8),dp(18),dp(18));
        ScrollView scroll=new ScrollView(this); scroll.setFillViewport(true); scroll.addView(controls);
        root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));

        base.setOnClickListener(v->showBase()); effects.setOnClickListener(v->showEffects()); art.setOnClickListener(v->showArtwork()); export.setOnClickListener(v->createExportDocument());
        showBase(); setContentView(root);
    }

    void clear(){ controls.removeAllViews(); }
    void heading(String s){ TextView t=text(s,17); t.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL); t.setPadding(dp(2),dp(8),0,dp(2)); t.setTypeface(null,1); controls.addView(t,new LinearLayout.LayoutParams(-1,dp(44))); }

    void showBase(){
        clear(); heading("Vinyl colour");
        LinearLayout row=new LinearLayout(this); row.setGravity(Gravity.CENTER);
        int[] cs={0xFF111111,0xFFF4F4F4,0xFFE91E63,0xFF7B1FA2,0xFF1565C0,0xFFFF9800,0xFF2E7D32};
        for(int c:cs){ TextView sw=text("",1); sw.setBackground(round(c,50)); LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(dp(42),dp(42)); p.setMargins(dp(4),0,dp(4),dp(8)); row.addView(sw,p); sw.setOnClickListener(v->{vinyl.vinylColor=c; vinyl.invalidate();}); }
        controls.addView(row);

        heading("Label style");
        LinearLayout labels=new LinearLayout(this); for(int i=0;i<4;i++){ final int x=i; Button b=pill(new String[]{"Dark","Red","Green","Cream"}[i]); labels.addView(b,new LinearLayout.LayoutParams(0,dp(46),1)); b.setOnClickListener(v->{vinyl.label=x;vinyl.invalidate();}); } controls.addView(labels);

        heading("Shape");
        LinearLayout shapes=new LinearLayout(this); String[] ss={"LP","Rounded","Heart","Star"};
        for(int i=0;i<4;i++){ final int x=i; Button b=pill(ss[i]); shapes.addView(b,new LinearLayout.LayoutParams(0,dp(46),1)); b.setOnClickListener(v->{vinyl.shape=x;vinyl.invalidate();}); } controls.addView(shapes);

        heading("Centre label size"); addSeek("Size",18,42,vinyl.labelSize,v->{vinyl.labelSize=v;vinyl.invalidate();});
    }

    void showEffects(){
        clear(); heading("Vinyl effects");
        addSeek("Gloss",0,100,vinyl.gloss,v->{vinyl.gloss=v;vinyl.invalidate();});
        addSeek("Grooves",0,100,vinyl.groove,v->{vinyl.groove=v;vinyl.invalidate();});
        addSeek("Grain",0,100,vinyl.grain,v->{vinyl.grain=v;vinyl.invalidate();});
        addSeek("Highlight",0,100,vinyl.highlight,v->{vinyl.highlight=v;vinyl.invalidate();});
        addSeek("Transparency",0,75,vinyl.transparency,v->{vinyl.transparency=v;vinyl.invalidate();});
        heading("Preview");
        Button spin=pill(vinyl.spinning?"Pause spin":"Spin record"); controls.addView(spin,new LinearLayout.LayoutParams(-1,dp(48)));
        spin.setOnClickListener(v->{vinyl.spinning=!vinyl.spinning; spin.setText(vinyl.spinning?"Pause spin":"Spin record"); vinyl.invalidate();});
    }

    void showArtwork(){
        clear(); heading("Album artwork");
        TextView hint=text(vinyl.cover==null?"Choose an image first.":"Adjust how the image sits on the sleeve and centre label.",14); hint.setTextColor(0xFFCCBFC7); hint.setGravity(Gravity.LEFT); controls.addView(hint,new LinearLayout.LayoutParams(-1,dp(42)));
        addSeek("Crop zoom",100,220,vinyl.artZoom,v->{vinyl.artZoom=v;vinyl.invalidate();});
        addSeek("Horizontal",-100,100,vinyl.artX,v->{vinyl.artX=v;vinyl.invalidate();});
        addSeek("Vertical",-100,100,vinyl.artY,v->{vinyl.artY=v;vinyl.invalidate();});
        heading("Centre label artwork");
        Button toggle=pill(vinyl.useArtworkOnLabel?"Artwork label: ON":"Artwork label: OFF"); controls.addView(toggle,new LinearLayout.LayoutParams(-1,dp(48)));
        toggle.setOnClickListener(v->{vinyl.useArtworkOnLabel=!vinyl.useArtworkOnLabel;toggle.setText(vinyl.useArtworkOnLabel?"Artwork label: ON":"Artwork label: OFF");vinyl.invalidate();});
    }

    interface IntSetter{void set(int v);}
    void addSeek(String name,int min,int max,int value,IntSetter set){
        TextView l=text(name+"  "+value,14); l.setGravity(Gravity.LEFT); controls.addView(l,new LinearLayout.LayoutParams(-1,dp(30)));
        SeekBar s=new SeekBar(this); s.setMax(max-min); s.setProgress(value-min); controls.addView(s,new LinearLayout.LayoutParams(-1,dp(40)));
        s.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){ public void onProgressChanged(SeekBar b,int p,boolean f){int v=p+min;l.setText(name+"  "+v);set.set(v);} public void onStartTrackingTouch(SeekBar b){} public void onStopTrackingTouch(SeekBar b){} });
    }

    void pickImage(){ Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT); i.setType("image/*"); i.addCategory(Intent.CATEGORY_OPENABLE); startActivityForResult(i,42); }
    void createExportDocument(){ Intent i=new Intent(Intent.ACTION_CREATE_DOCUMENT); i.addCategory(Intent.CATEGORY_OPENABLE); i.setType("image/png"); i.putExtra(Intent.EXTRA_TITLE,"vinyl-studio.png"); startActivityForResult(i,77); }

    @Override protected void onActivityResult(int r,int c,Intent d){
        super.onActivityResult(r,c,d);
        if(c!=RESULT_OK||d==null) return;
        if(r==42){
            try{ Uri u=d.getData(); final int flags=d.getFlags() & Intent.FLAG_GRANT_READ_URI_PERMISSION; try{getContentResolver().takePersistableUriPermission(u,flags);}catch(Exception ignored){}
                InputStream in=getContentResolver().openInputStream(u); Bitmap b=BitmapFactory.decodeStream(in); in.close(); vinyl.cover=b; vinyl.invalidate(); showArtwork();
            }catch(Exception e){Toast.makeText(this,"Couldn't load image",Toast.LENGTH_SHORT).show();}
        } else if(r==77){
            try{ Uri u=d.getData(); Bitmap out=vinyl.renderExport(1600); OutputStream os=getContentResolver().openOutputStream(u); out.compress(Bitmap.CompressFormat.PNG,100,os); os.flush(); os.close(); Toast.makeText(this,"PNG exported",Toast.LENGTH_SHORT).show(); }
            catch(Exception e){Toast.makeText(this,"Export failed",Toast.LENGTH_SHORT).show();}
        }
    }

    public class VinylView extends View {
        Paint p=new Paint(Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG); Bitmap cover;
        int vinylColor=0xFF111111,label=0,shape=0,gloss=72,groove=70,grain=10,highlight=68,transparency=0,labelSize=29;
        int artZoom=120,artX=0,artY=0; boolean useArtworkOnLabel=true,spinning=false; float angle=0; Random rng=new Random(12);
        VinylView(Context c){super(c); setLayerType(LAYER_TYPE_SOFTWARE,null);}

        @Override protected void onDraw(Canvas c){ super.onDraw(c); drawScene(c,getWidth(),getHeight(),false); if(spinning){angle+=1.35f;postInvalidateDelayed(16);} }

        Bitmap renderExport(int size){ Bitmap b=Bitmap.createBitmap(size,size,Bitmap.Config.ARGB_8888); Canvas c=new Canvas(b); drawScene(c,size,size,true); return b; }

        void drawScene(Canvas c,float w,float h,boolean export){
            p.setStyle(Paint.Style.FILL); p.setShader(new LinearGradient(0,0,w,h,0xFF130B11,0xFF2B1021,Shader.TileMode.CLAMP)); c.drawRect(0,0,w,h,p); p.setShader(null);
            float cx=w*(export ? .61f : .67f), cy=h*.49f, r=Math.min(w,h)*(export ? .28f : .34f);
            float sleeveSize=Math.min(w,h)*(export ? .50f : .36f), sx=w*(export ? .07f : .06f), sy=h*(export ? .23f : .13f);
            if(cover!=null){
                p.setShadowLayer(Math.max(8,w*.015f),0,w*.012f,0x99000000); drawCoverCrop(c,cover,new RectF(sx,sy,sx+sleeveSize,sy+sleeveSize),artZoom,artX,artY,p); p.clearShadowLayer();
            } else {
                p.setColor(0xFF392A35); c.drawRoundRect(new RectF(sx,sy,sx+sleeveSize,sy+sleeveSize),w*.015f,w*.015f,p);
                p.setColor(0xFF9E8B98); p.setTextAlign(Paint.Align.CENTER); p.setTextSize(sleeveSize*.055f); c.drawText("CHOOSE ARTWORK",sx+sleeveSize/2,sy+sleeveSize/2,p);
            }
            c.save(); c.rotate(angle,cx,cy); drawRecord(c,cx,cy,r); c.restore();
        }

        void drawRecord(Canvas c,float x,float y,float r){
            p.setStyle(Paint.Style.FILL); int alpha=255-(transparency*255/100); p.setColor((alpha<<24)|(vinylColor&0x00FFFFFF));
            Path shapePath=recordPath(x,y,r); p.setShadowLayer(r*.055f,0,r*.04f,0x99000000); c.drawPath(shapePath,p); p.clearShadowLayer();

            if(groove>0){ p.setStyle(Paint.Style.STROKE); int rings=12+(groove*22/100); for(int i=0;i<rings;i++){ float rr=r*(.96f-i*(.70f/rings)); int a=12+(groove*40/100); p.setColor((a<<24)|0x00FFFFFF); p.setStrokeWidth(Math.max(1,r*.0035f)); c.drawCircle(x,y,rr,p);} }

            if(gloss>0){ p.setStyle(Paint.Style.FILL); int a=gloss*95/100; p.setShader(new RadialGradient(x-r*.25f,y-r*.35f,r*1.35f,new int[]{(a<<24)|0x00FFFFFF,0x00FFFFFF,0x33000000},new float[]{0f,.55f,1f},Shader.TileMode.CLAMP)); c.drawPath(shapePath,p); p.setShader(null); }
            if(highlight>0){ p.setStyle(Paint.Style.STROKE); p.setStrokeWidth(r*.11f); p.setStrokeCap(Paint.Cap.ROUND); p.setColor(((highlight*90/100)<<24)|0x00FFFFFF); RectF arc=new RectF(x-r*.78f,y-r*.78f,x+r*.78f,y+r*.78f); c.drawArc(arc,205,65,false,p); p.setStrokeCap(Paint.Cap.BUTT); }
            if(grain>0){ p.setStyle(Paint.Style.FILL); rng.setSeed(22); int dots=grain*3; for(int i=0;i<dots;i++){ double a=rng.nextDouble()*Math.PI*2; float rr=(float)Math.sqrt(rng.nextDouble())*r*.95f; float gx=x+(float)Math.cos(a)*rr, gy=y+(float)Math.sin(a)*rr; p.setColor(0x22FFFFFF); c.drawCircle(gx,gy,Math.max(1,r*.003f),p);} }

            float lr=r*(labelSize/100f);
            p.setStyle(Paint.Style.FILL);
            if(useArtworkOnLabel && cover!=null){ Path clip=new Path(); clip.addCircle(x,y,lr,Path.Direction.CW); c.save(); c.clipPath(clip); drawCoverCrop(c,cover,new RectF(x-lr,y-lr,x+lr,y+lr),artZoom,artX,artY,p); c.restore(); p.setColor(0x33000000); c.drawCircle(x,y,lr,p); }
            else { p.setColor(labelColor()); c.drawCircle(x,y,lr,p); }
            p.setStyle(Paint.Style.STROKE); p.setStrokeWidth(Math.max(1,r*.007f)); p.setColor(0x99FFFFFF); c.drawCircle(x,y,lr*.93f,p);
            p.setStyle(Paint.Style.FILL); p.setColor(0xFF080808); c.drawCircle(x,y,r*.038f,p);
            p.setTextAlign(Paint.Align.CENTER); p.setColor(Color.WHITE); p.setTypeface(Typeface.DEFAULT_BOLD); p.setTextSize(r*.072f); c.drawText("VINYL",x,y-lr*.12f,p); p.setTypeface(Typeface.DEFAULT); p.setTextSize(r*.045f); c.drawText("STUDIO",x,y+lr*.20f,p);
        }

        Path recordPath(float x,float y,float r){ Path q=new Path(); if(shape==0) q.addCircle(x,y,r,Path.Direction.CW); else if(shape==1) q.addRoundRect(new RectF(x-r,y-r,x+r,y+r),r*.22f,r*.22f,Path.Direction.CW); else if(shape==2){ q.moveTo(x,y+r*.82f); q.cubicTo(x-r*1.14f,y+r*.02f,x-r*.68f,y-r*.82f,x,y-r*.28f); q.cubicTo(x+r*.68f,y-r*.82f,x+r*1.14f,y+r*.02f,x,y+r*.82f); q.close(); } else { for(int i=0;i<10;i++){ double a=-Math.PI/2+i*Math.PI/5;float rr=(i%2==0)?r:r*.48f;float px=x+(float)Math.cos(a)*rr,py=y+(float)Math.sin(a)*rr;if(i==0)q.moveTo(px,py);else q.lineTo(px,py);}q.close(); } return q; }
        int labelColor(){ switch(label){case 1:return 0xFFB82B35;case 2:return 0xFF356D55;case 3:return 0xFFE8D8BC;default:return 0xFF1B181C;} }

        void drawCoverCrop(Canvas c,Bitmap b,RectF dst,int zoom,int offX,int offY,Paint paint){
            float target=dst.width(); float scale=Math.max(target/b.getWidth(),target/b.getHeight())*(zoom/100f); float sw=dst.width()/scale, sh=dst.height()/scale;
            float maxX=Math.max(0,b.getWidth()-sw), maxY=Math.max(0,b.getHeight()-sh); float cx=b.getWidth()/2f+(offX/100f)*maxX/2f, cy=b.getHeight()/2f+(offY/100f)*maxY/2f;
            RectF src=new RectF(cx-sw/2f,cy-sh/2f,cx+sw/2f,cy+sh/2f); if(src.left<0)src.offset(-src.left,0); if(src.top<0)src.offset(0,-src.top); if(src.right>b.getWidth())src.offset(b.getWidth()-src.right,0); if(src.bottom>b.getHeight())src.offset(0,b.getHeight()-src.bottom);
            c.drawBitmap(b,src,dst,paint);
        }
    }
}
