package lab6;
import java.awt.*;
import java.awt.geom.*;
public class CustomStroke implements Stroke {

	private float waveLength = 6.0f;
	private float amplitude = 3.0f;
    private Stroke stroke = new BasicStroke();
	public CustomStroke(float a) {
		this.waveLength = 2 * a;
		this.amplitude = a;
	}
	public CustomStroke() {};
    public Shape createStrokedShape(Shape shape) {
		GeneralPath strokedShape = new GeneralPath();
	float coords[] = new float[6];
	float moveX = 0, moveY = 0;
	float lastX = 0, lastY = 0;
	float thisX = 0, thisY = 0;
	int type = 0;
    float next = 0;

	for(PathIterator i = shape.getPathIterator(null); !i.isDone(); i.next()) {
		type = i.currentSegment( coords );
		switch( type ){
		case PathIterator.SEG_MOVETO:
			moveX = lastX = coords[0];
			moveY = lastY = coords[1];
			strokedShape.moveTo( moveX, moveY );
			break;

		case PathIterator.SEG_CLOSE:
			coords[0] = moveX;
			coords[1] = moveY;
			strokedShape.closePath();
			break;
			
		case PathIterator.SEG_LINETO:
			thisX = coords[0];
			thisY = coords[1];
			float dx = thisX-lastX;
			float dy = thisY-lastY;
			float distance = (float)Math.sqrt( dx*dx + dy*dy );
			if ( distance >= 0 ) {
				float r = 1.0f/distance;
				float angle = (float)Math.atan2( dy, dx );
				float n = (float) Math.ceil( distance / waveLength);
				float currWaveLength = distance / n;
				amplitude = currWaveLength / 2;
				while ( distance > next ) {
					float x = lastX + next*dx*r;
					float y = lastY + next*dy*r;
					x += amplitude*dx*r;
					y += amplitude*dy*r;
					strokedShape.lineTo( x, y );
					x += Math.sqrt(2) * amplitude * Math.sin(Math.PI / 4 - angle);
					y += Math.sqrt(2) * amplitude * Math.cos(Math.PI / 4 - angle);
                    strokedShape.lineTo( x , y );
                    x += amplitude * Math.sin(angle);
                    y -= amplitude * Math.cos(angle);
                    strokedShape.lineTo(x, y);
                	next += currWaveLength;
			}
			lastX = thisX;
			lastY = thisY;
			next = 0;
            if ( type == PathIterator.SEG_CLOSE )
            	strokedShape.closePath();
			break;
		}
	}
	}
	return stroke.createStrokedShape( strokedShape );
    }
}
