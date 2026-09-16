package com.wang.tradingplatform.utils;

import com.wang.tradingplatform.exception.BusinessException;
import com.wang.tradingplatform.pojo.entity.ItemQueryParam;

/**
 * 参数校验工具类。
 * <p>
 * 所有校验不通过时统一抛出 {@link BusinessException}（运行时异常），
 * 异常信息中说明“因为什么不对”，由全局异常处理器捕获后返回给前端。
 */
public final class ParamUtil {

    private ParamUtil() {
    }

    /**
     * 校验对象不能为 null
     */
    public static void notNull(Object value, String fieldName) {
        if (value == null) {
            throw new BusinessException(fieldName + "不能为空");
        }
    }

    /**
     * 校验字符串不能为 null 或空白
     */
    public static void notBlank(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new BusinessException(fieldName + "不能为空");
        }
    }

    /**
     * 校验 Long 类型的 id 必须非空且大于 0
     */
    public static void positive(Long value, String fieldName) {
        if (value == null || value <= 0) {
            throw new BusinessException(fieldName + "不能为空或必须大于0");
        }
    }

    /**
     * 校验 Integer 类型的 id 必须非空且大于 0
     */
    public static void positive(Integer value, String fieldName) {
        if (value == null || value <= 0) {
            throw new BusinessException(fieldName + "不能为空或必须大于0");
        }
    }

    /**
     * 校验分页查询参数：页码与每页数量必须合法
     */
    public static void checkPage(ItemQueryParam param) {
        notNull(param, "查询参数");
        if (param.getPageNumber() == null || param.getPageNumber() < 1) {
            throw new BusinessException("页码必须大于等于1");
        }
        if (param.getPageSize() == null || param.getPageSize() < 1 || param.getPageSize() > 100) {
            throw new BusinessException("每页数量必须在1到100之间");
        }
    }
}
