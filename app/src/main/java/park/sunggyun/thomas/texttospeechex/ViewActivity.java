package park.sunggyun.thomas.texttospeechex;

import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import park.sunggyun.thomas.texttospeechex.databinding.ActivityViewBinding;

public class ViewActivity extends AppCompatActivity {

    private ActivityViewBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_view);
        Glide.with(this).load(R.drawable.image).into(binding.imgView);
        String videoRootPath = "android.resource://" + getPackageName() + "/";
        binding.videoView.setVideoURI(Uri.parse(videoRootPath + R.raw.video));
        binding.videoView.start();

        binding.videoView.setOnCompletionListener(mp -> binding.videoView.start());
    }
}
