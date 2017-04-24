package april.yun.other;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

/**
 * @author yun.
 * @date 2017/4/22
 * @des [一句话描述]
 * @since [https://github.com/ZuYun]
 * <p><a href="https://github.com/ZuYun">github</a>
 */
public class SavedState extends View.BaseSavedState {
    public int currentPosition;


    public SavedState(Parcelable superState) {
        super(superState);
    }


    private SavedState(Parcel in) {
        super(in);
        currentPosition = in.readInt();
    }


    @Override public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(currentPosition);
    }


    public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
        @Override public SavedState createFromParcel(Parcel in) {
            return new SavedState(in);
        }


        @Override public SavedState[] newArray(int size) {
            return new SavedState[size];
        }
    };
}
