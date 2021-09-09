package com.mycompany.dao;

import com.mycompany.entity.Task;

import java.io.Serializable;
import java.util.List;


public interface CustomDao<T> extends Serializable {

        public void persist(T entity);

        public void update(T entity);

        public T findById(Integer id);

        public void delete(T entity);

        public List<T> findAll();

        public void deleteAll();
}
