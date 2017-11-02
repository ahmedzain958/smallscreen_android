package com.techsignage.techsignmeetings.Models.Interfaces;

import com.techsignage.techsignmeetings.Models.HourModel;

import java.util.List;

/**
 * Created by heat on 5/23/2017.
 */

public interface hourCallback {
    void selectedItem(HourModel selectedModel, List<HourModel> lst);
}
