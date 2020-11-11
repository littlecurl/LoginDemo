package edu.heuet.android.logindemo.service;

import edu.heuet.android.logindemo.dataobject.NewsDO;

import java.util.List;

public interface NewsService {
    NewsDO selectDetailById(long id);

    List<NewsDO> selectDetailByTitle(String title);

    List<NewsDO> selectAll();
}
