package Adapter_and_fragments;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class fragmentAdapterInstallation extends FragmentStateAdapter {
    public fragmentAdapterInstallation(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position)
    {
            switch(position)
            {
                case 1:
                    return new fragment2repair();
                case 2:
                    return new fragment3cleaning();
                case 3:
                    return new fragment4marketplace();
                default:
                    return new fragment1Installer();
            }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
