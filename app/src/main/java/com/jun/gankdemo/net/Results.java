package com.jun.gankdemo.net;

import com.jun.gankdemo.utils.LogUtil;

import java.util.List;

import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import retrofit2.adapter.rxjava2.Result;

public class Results {

    public static Predicate<Result<?>> DATA_FUNC = new Predicate<Result<?>>() {
        @Override
        public boolean test(Result<?> result) throws Exception {
            return !result.isError() && result.response().isSuccessful();
        }
    };

    public static Predicate<Result<?>> isSuccess(){
        return DATA_FUNC;
    }

    public static Predicate<List<?>> IMAGE_FUN = new Predicate<List<?>>() {
        @Override
        public boolean test(List<?> list) throws Exception {
            return list.size() != 0;
        }
    };

    public static Predicate<List<?>> isNull(){
        return IMAGE_FUN;
    }

    private Results(){
        throw new AssertionError("no instances");
    }
}
