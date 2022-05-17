package Adapter_and_fragments;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import Adapter_and_fragments.fragment1Installer;
import Adapter_and_fragments.fragment2repair;
import Adapter_and_fragments.fragment3cleaning;
import Adapter_and_fragments.fragment4marketplace;

public class fragmentAdapterHistory extends FragmentStateAdapter {
    public fragmentAdapterHistory(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch(position)
        {
            case 1:
                return new fragment2complete();

            default:
                return new fragment1cancelled();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
