package org.ispw.fastridetrack.dao.Adapter;

import org.ispw.fastridetrack.bean.MapRequestBean;
import org.ispw.fastridetrack.model.Map;

public interface MapService {
    Map calculateRoute(MapRequestBean requestBean);
}




