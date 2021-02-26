package org.jellyfin.androidtv.ui.presentation;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.leanback.widget.BaseCardView;

import org.jellyfin.androidtv.R;
import org.jellyfin.androidtv.TvApp;
import org.jellyfin.androidtv.databinding.ImageCardViewBinding;
import org.jellyfin.androidtv.preference.UserPreferences;
import org.jellyfin.androidtv.preference.constant.WatchedIndicatorBehavior;
import org.jellyfin.androidtv.ui.itemhandling.BaseRowItem;
import org.jellyfin.androidtv.util.TimeUtils;
import org.jellyfin.androidtv.util.Utils;

import static org.koin.java.KoinJavaComponent.get;

/**
 * Modified ImageCard with no fade on the badge
 * A card view with an {@link ImageView} as its main region.
 */
public class MyImageCardView extends BaseCardView {
    private ImageCardViewBinding binding = ImageCardViewBinding.inflate(LayoutInflater.from(getContext()), this);
    private ImageView mBanner;
    private int BANNER_SIZE = Utils.convertDpToPixel(getContext(), 50);
    private int noIconMargin = Utils.convertDpToPixel(getContext(), 5);

    public MyImageCardView(Context context, boolean showInfo) {
        super(context, null, R.attr.imageCardViewStyle);

        if (!showInfo) {
            setCardType(CARD_TYPE_MAIN_ONLY);
        }

        binding.mainImage.setClipToOutline(true);
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, @Nullable Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        if (gainFocus) {
            binding.titleText.setTextColor(ContextCompat.getColor(getContext(), R.color.lb_basic_card_title_text_color));
            binding.contentText.setTextColor(ContextCompat.getColor(getContext(), R.color.lb_basic_card_title_text_color));
            binding.badgeText.setTextColor(ContextCompat.getColor(getContext(), R.color.lb_basic_card_title_text_color));
            binding.extraBadge.setAlpha(1.0f);
        } else {
            binding.titleText.setTextColor(ContextCompat.getColor(getContext(), R.color.lb_basic_card_title_text_color_inactive));
            binding.contentText.setTextColor(ContextCompat.getColor(getContext(), R.color.lb_basic_card_title_text_color_inactive));
            binding.badgeText.setTextColor(ContextCompat.getColor(getContext(), R.color.lb_basic_card_title_text_color_inactive));
            binding.extraBadge.setAlpha(0.25f);
        }
    }

    public void setBanner(int bannerResource) {
        if (mBanner == null) {
            mBanner = new ImageView(getContext());
            mBanner.setLayoutParams(new ViewGroup.LayoutParams(BANNER_SIZE, BANNER_SIZE));

            ((ViewGroup) getRootView()).addView(mBanner);
        }

        mBanner.setImageResource(bannerResource);
        mBanner.setVisibility(VISIBLE);
    }

    public final ImageView getMainImageView() {
        return binding.mainImage;
    }

    public void setPlayingIndicator(boolean playing) {
        if (playing) {
            // TODO use decent animation for equalizer icon
            binding.extraBadge.setBackgroundResource(R.drawable.ic_play);
            binding.extraBadge.setVisibility(VISIBLE);
        } else {
            binding.extraBadge.setBackgroundResource(R.drawable.blank10x10);
        }
    }

    public void setMainImageDimensions(int width, int height) {
        ViewGroup.LayoutParams lp = binding.mainImage.getLayoutParams();
        lp.width = width;
        lp.height = height;
        binding.mainImage.setLayoutParams(lp);
        if (mBanner != null) mBanner.setX(width - BANNER_SIZE);
        ViewGroup.LayoutParams lp2 = binding.resumeProgress.getLayoutParams();
        lp2.width = width;
        binding.resumeProgress.setLayoutParams(lp2);
    }

    public void setTitleText(CharSequence text) {
        if (binding.titleText == null) {
            return;
        }

        binding.titleText.setText(text);
        setTextMaxLines();
    }

    public void setOverlayText(String text) {
        if (getCardType() == BaseCardView.CARD_TYPE_MAIN_ONLY) {
            binding.overlayText.setText(text);
            binding.nameOverlay.setVisibility(VISIBLE);
            hideIcon();
        } else {
            binding.nameOverlay.setVisibility(GONE);
        }
    }

    public void setOverlayInfo(BaseRowItem item) {
        if (binding.overlayText == null) return;

        if (getCardType() == BaseCardView.CARD_TYPE_MAIN_ONLY && item.showCardInfoOverlay()) {
            switch (item.getBaseItemType()) {
                case Photo:
                    binding.overlayText.setText(item.getBaseItem().getPremiereDate() != null ? android.text.format.DateFormat.getDateFormat(TvApp.getApplication()).format(TimeUtils.convertToLocalDate(item.getBaseItem().getPremiereDate())) : item.getFullName());
                    binding.icon.setImageResource(R.drawable.ic_camera);
                    break;
                case PhotoAlbum:
                    binding.overlayText.setText(item.getFullName());
                    binding.icon.setImageResource(R.drawable.ic_photos);
                    break;
                case Video:
                    binding.overlayText.setText(item.getFullName());
                    binding.icon.setImageResource(R.drawable.ic_movie);
                    break;
                case Playlist:
                case MusicArtist:
                case Person:
                    binding.overlayText.setText(item.getFullName());
                    hideIcon();
                    break;
                default:
                    binding.overlayText.setText(item.getFullName());
                    binding.icon.setImageResource(item.isFolder() ? R.drawable.ic_folder : R.drawable.blank30x30);
                    break;
            }
            binding.overlayCount.setText(item.getChildCountStr());
            binding.nameOverlay.setVisibility(VISIBLE);
        } else {
            binding.nameOverlay.setVisibility(GONE);
        }
    }

    protected void hideIcon() {
        binding.icon.setVisibility(GONE);
        RelativeLayout.LayoutParams parms = (RelativeLayout.LayoutParams) binding.overlayText.getLayoutParams();
        parms.rightMargin = noIconMargin;
        parms.leftMargin = noIconMargin;
        binding.overlayText.setLayoutParams(parms);
    }

    public CharSequence getTitleText() {
        if (binding.titleText == null) {
            return null;
        }

        return binding.titleText.getText();
    }

    public void setContentText(CharSequence text) {
        if (binding.contentText == null) {
            return;
        }

        binding.contentText.setText(text);
        setTextMaxLines();
    }

    public CharSequence getContentText() {
        if (binding.contentText == null) {
            return null;
        }

        return binding.contentText.getText();
    }

    public void setRating(String rating) {
        if (rating != null) {
            binding.badgeText.setText(rating);
            binding.badgeText.setVisibility(VISIBLE);
        } else {
            binding.badgeText.setText("");
            binding.badgeText.setVisibility(GONE);
        }
    }

    public void setBadgeImage(Drawable drawable) {
        if (binding.extraBadge == null) {
            return;
        }

        if (drawable != null) {
            binding.extraBadge.setImageDrawable(drawable);
            binding.extraBadge.setVisibility(View.VISIBLE);
        } else {
            binding.extraBadge.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean hasOverlappingRendering() {
        return false;
    }

    private void setTextMaxLines() {
        if (TextUtils.isEmpty(getTitleText())) {
            binding.contentText.setMaxLines(2);
        } else {
            binding.contentText.setMaxLines(1);
        }

        if (TextUtils.isEmpty(getContentText())) {
            binding.titleText.setMaxLines(2);
        } else {
            binding.titleText.setMaxLines(1);
        }
    }

    public void clearBanner() {
        if (mBanner != null) {
            mBanner.setVisibility(GONE);
        }
    }

    public void setUnwatchedCount(int count) {
        WatchedIndicatorBehavior showIndicator = get(UserPreferences.class).get(UserPreferences.Companion.getWatchedIndicatorBehavior());
        if (count > 0) {
            if (showIndicator != WatchedIndicatorBehavior.ALWAYS) {
                binding.unwatchedCount.setVisibility(INVISIBLE);
                binding.watchedIndicator.setVisibility(INVISIBLE);
            }
            else {
                binding.unwatchedCount.setText(count > 99 ? getContext().getString(R.string.watch_count_overflow) : Integer.toString(count));
                binding.unwatchedCount.setVisibility(VISIBLE);
                binding.watchedIndicator.setVisibility(VISIBLE);
            }
            binding.watched.setVisibility(INVISIBLE);
        } else if (count == 0) {
            if (showIndicator != WatchedIndicatorBehavior.NEVER) {
                binding.watched.setVisibility(VISIBLE);
                binding.watchedIndicator.setVisibility(VISIBLE);
            }
            else {
                binding.watched.setVisibility(INVISIBLE);
                binding.watchedIndicator.setVisibility(INVISIBLE);
            }
            binding.unwatchedCount.setVisibility(INVISIBLE);
        } else {
            binding.watchedIndicator.setVisibility(GONE);
        }
    }

    public void setProgress(int pct) {
        if (pct > 0) {
            binding.resumeProgress.setProgress(pct);
            binding.resumeProgress.setVisibility(VISIBLE);
        } else {
            binding.resumeProgress.setVisibility(GONE);
        }
    }

    public void showFavIcon(boolean show) {
        binding.favIcon.setVisibility(show ? VISIBLE : GONE);
    }
}
