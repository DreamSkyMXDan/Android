
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * Created by daniel shen on 3/16/2017.
 */

public class SignatureView extends ImageView {
	public final static int DISPLAY_ONLY = 0;
	public final static int INTERACTIVE = 1;
	public final static int DEFAULT_WIDTH = 200;
	public final static int DEFAULT_HEIGHT = 100;
	public final static int DEFAULT_XY_RATIO = 2;
	public final static float DEFAULT_TOLERANCE = 4f;
	public final static int PADDING_TOP = 10;
	public final static int PADDING_LEFT = 20;
	
	private float _tolerance = DEFAULT_TOLERANCE;
	// the whole graph may consist of multiple connected graphs
	private ArrayList<ConnectedGraph> _sig = new ArrayList<ConnectedGraph>();
	private  Context _context;
	private Paint mPaint = new Paint();
	private Bitmap mBitmap;
	private Canvas mCanvas;
	private Path mPath;
	private Paint mBitmapPaint;
	private float xscale = 1f;
	private float yscale = 1f;
	int _width = DEFAULT_WIDTH;
	private int _height = DEFAULT_HEIGHT;
	private int _mode = 0;
	private int strokeColor = Color.BLUE; 
	private ConnectedGraph _cgCurrent;
	private float _px, _py;

	// each connected graph stands for a one time smooth move
	protected class ConnectedGraph {

        private ArrayList<Point> points = new ArrayList<Point>();
        
        public ConnectedGraph() {
        }

        public ConnectedGraph(Point p) {
            points.add(p);
        }
        
        public ConnectedGraph(String cgString) {
            int len = cgString.length();

        	while (len > 0) {
        		try {
	        		String x = cgString.substring(0, 2);
	        		String y = cgString.substring(2, 4);		
	        		int xs = (int)(Integer.parseInt(x, 16));
	        		int ys = (int)(Integer.parseInt(y, 16));
	        		addPoint(new Point(xs, ys));
        		} catch (Exception e) {
        			//silient
        		}
        		cgString = cgString.substring(4);
        		len = cgString.length();
        	}

        }
        
        public int addPoint(Point p) {
            points.add(p);
            return (points.size() - 1);
        }
        
        public Point getPoint(int index) {
            Point p = null;
            if (index < points.size()) {
                p = (Point) (points.get(index));
            }
            return p;
        }

        public boolean setPoint(int index, int x, int y) {
            boolean ret = false;
            if (index < points.size()) {
                Point p = (Point) (points.get(index));
                if (p != null) {
                    p.x = x;
                    p.y = y;
                    ret = true;
                }
            }
            return ret;
        }

        public int getOrder() {
            return points.size();
        }
        
        public Path getPath(float xscale, float yscale) {
        	Path ret = new Path();
        	float px = -1, py = -1;
        	for (Point p : points) {
        		float x = xscale * p.x, y = yscale * p.y; 
	    		if (px == -1) {
	    			ret.moveTo(x, y);
	    		} else {
	    			float dx = Math.abs(x - px);
	    	        float dy = Math.abs(y - py);
	    	        if (dx >= _tolerance || dy >= _tolerance) {
		    			ret.quadTo(px, py, (x + px)/2, (y + py)/2);
	    	        } else {
	    	        	ret.lineTo(x, y);
	    	        }
	    		}
	    		px = x;
	    		py = y;
        	}
        	return ret;
        }
        
        public String toHexString() {
            StringBuffer sb = new StringBuffer();
            try {
            	for (Point p : points) {
                    if (p.x > -1) {
                        if (p.x < 16) {
                            sb.append('0');
                        }
                        sb.append(Integer.toHexString(p.x));
                    } else {
                        sb.append("00");
                    }
                    if (p.y > -1) {
                        if (p.y < 16) {
                            sb.append('0');
                        }
                        sb.append(Integer.toHexString(p.y));
                    } else {
                        sb.append("00");
                    }
            	}
            } catch (Exception e) {

            }
            return sb.toString();
        }
    }
	
	public SignatureView(Context context, int mode) {
		super(context);
		_context = context;
		_mode = mode;
		if (_mode == INTERACTIVE) {
			setImageResource(R.drawable.signbox);
			setScaleType(ScaleType.FIT_XY);
			setId(R.id.ivSign);
	        mPath = new Path();
	        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
		}
	}

	public SignatureView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SignatureView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld) {
        super.onSizeChanged(xNew, yNew, xOld, yOld);
        if (xNew > 0) {
        	_width = xNew - PADDING_LEFT;
        }
        if (yNew > 0) {
        	_height = yNew - PADDING_TOP;
        }
        xscale = _width / (float)DEFAULT_WIDTH;
        yscale = _height / (float)DEFAULT_HEIGHT;
        _tolerance = yscale * DEFAULT_TOLERANCE;
        if (mBitmap == null) {
			mBitmap = Bitmap.createBitmap(_width, _height, Bitmap.Config.ARGB_8888);
	        mCanvas = new Canvas(mBitmap);
        }
    }
	
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(strokeColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(3);
        
        if (_mode == DISPLAY_ONLY) {
			if (_sig.size() > 0) {
				for (int i=0;i<_sig.size();i++) {
					canvas.drawPath(_sig.get(i).getPath(xscale, yscale), mPaint);
				}
			}
        } else {  
            if (mBitmap == null) {
    			mBitmap = Bitmap.createBitmap(_width, _height, Bitmap.Config.ARGB_8888);
    	        mCanvas = new Canvas(mBitmap);
            }
            canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint); 
        	canvas.drawPath(mPath, mPaint);
        }
	}    
	// time to create a new connected graph
    private void touch_start(float x, float y) {
        mPath.reset();
        mPath.moveTo(x, y);
        _px = x;
        _py = y;
        _cgCurrent = new ConnectedGraph(new Point((int)(x / xscale), (int)(y / yscale)));
    }
    
    private void touch_move(float x, float y) {
    	try {
	        float dx = Math.abs(x - _px);
	        float dy = Math.abs(y - _py);
	        if (dx >= _tolerance || dy >= _tolerance) {
	            mPath.quadTo(_px, _py, (x + _px)/2, (y + _py)/2);
	        } else {
	        	mPath.lineTo(x, y);
	        }
	        _px = x;
	        _py = y;
	        _cgCurrent.addPoint(new Point((int)(x / xscale), (int)(y / yscale)));
    	} catch (Exception e){}
    }
    // time to add the connected graph to the list
    private void touch_up() {
        mPath.lineTo(_px, _py);
        _sig.add(_cgCurrent);
        mCanvas.drawPath(mPath, mPaint);
        mPath.reset();
    }
    
    @SuppressLint("ClickableViewAccessibility")
	@Override
    public boolean onTouchEvent(MotionEvent event) {
    	if (_mode == INTERACTIVE) {
	        float x = event.getX();
	        float y = event.getY();
	        
	        switch (event.getAction()) {
	            case MotionEvent.ACTION_DOWN:
	                touch_start(x, y);
	                invalidate();
	                break;
	            case MotionEvent.ACTION_MOVE:
	                touch_move(x, y);
	                invalidate();
	                break;
	            case MotionEvent.ACTION_UP:
	                touch_up();
	                invalidate();
	                break;
	        }
	        return true;
    	} else {
    		return super.onTouchEvent(event);
    	}
    }

	
	public String getValue() {
		StringBuffer sb = new StringBuffer();
        try {
            for (ConnectedGraph cg : _sig) {
            	if (sb.length() > 0) {
                    sb.append(D2LinkService.COMMA);
            	}
                sb.append(cg.toHexString());
            }
        } catch (Exception e) {
        }
        return sb.toString();
	}
	
	public void setValue(String value) {
		String[] cgs = value.split(D2LinkService.COMMA);
		_sig.clear();
		for (int i=0;i<cgs.length;i++) {
			ConnectedGraph cg = new ConnectedGraph(cgs[i]);
			_sig.add(cg);
		}
		invalidate();
	}
	
	public void clear() {
		if (_sig != null) {
			_sig.clear();
			mBitmap = null;
		}
		invalidate();
	}
}
