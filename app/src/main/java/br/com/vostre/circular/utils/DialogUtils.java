package br.com.vostre.circular.utils;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import java.util.List;

public class DialogUtils {

    public static DialogFragment getOpenedDialog(FragmentActivity activity) {
        List<Fragment> fragments = activity.getSupportFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment instanceof DialogFragment) {
                    return (DialogFragment) fragment;
                }
            }
        }

        return null;
    }

}
