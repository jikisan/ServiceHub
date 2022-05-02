package Adapter_and_fragments;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class fragmentAdapterListings extends FragmentStateAdapter {

    public fragmentAdapterListings(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if  (position == 1){
            return new fragment2Order();
        }

        return new fragment1Listings();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
