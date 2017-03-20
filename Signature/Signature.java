
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

@SuppressWarnings("deprecation")

/**
 * Created by daniel shen on 3/16/2017.
 */


public class Signature extends RelativeLayout {
	
	private TextView _label;
	SignatureView _ivSign;
	private int _mode = 0;
	private Context _context;

	public Signature(Context context, String labelText, int mode) {
		super(context);
		_context = context;
        setPadding(5, 5, 5, 5);
        _mode = mode;
		if (_mode == SignatureView.INTERACTIVE) {
			setSignatureView();
		} else {
    // inflate the signature layout to add it as a subview of a parentview
			LayoutInflater infalInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        infalInflater.inflate(R.layout.signature, this, true);
	        
	        _label = (TextView)findViewById(R.id.lblSign);
	        _label.setText(labelText);
	        
	        _ivSign = (SignatureView)findViewById(R.id.ivSign);
		}
	}

	public Signature(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	// redraw its signatureview
	public void setValue(String sig) {
		if (_ivSign != null) {
			_ivSign.setValue(sig);
		}
	}
	// construct an independent signatureview
	public void setSignatureView() {
		if (_ivSign != null) {
			removeView(_ivSign);
		}
		_ivSign = new SignatureView(_context, _mode);
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                LayoutParams.FILL_PARENT,
                LayoutParams.WRAP_CONTENT);
		lp.addRule(CENTER_IN_PARENT);
		addView(_ivSign, lp);
		_ivSign.setMinimumHeight(_ivSign._width / 3);
		invalidate();
	}
	
	public String getValue() {
		String ret = D2LinkService.EMPTY_STRING;
		if (_ivSign != null) {
			ret = _ivSign.getValue();
		}		
		return ret;
	}
	
	public String getLabel() {
		String ret = D2LinkService.EMPTY_STRING;
		if (_label != null) {
			ret = (String)_label.getText();
		}
		return ret;
	}
}
