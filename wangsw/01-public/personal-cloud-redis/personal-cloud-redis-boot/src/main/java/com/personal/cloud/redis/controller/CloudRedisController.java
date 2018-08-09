package com.personal.cloud.redis.controller;

import com.personal.cloud.redis.support.util.RestResultDto;
import com.personal.cloud.redis.service.ICloudRedisService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @date 2018-08-08
 * @author wsw
 * Created by wsw on 2018/8/8.
 */
@Api(value = "redis相关接口" , tags = "redis")
@RestController
public class CloudRedisController {

    public static  final Logger LOG = LoggerFactory.getLogger(CloudRedisController.class);

    @Autowired
    private ICloudRedisService  cloudRedisService;

    @ApiOperation(value = "redis新增键值对", notes = "1")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "key", value = "键", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "value", value = "值", required = true, paramType = "query", dataType = "String"),
    })
    @RequestMapping(value = "/addRedisString", method = RequestMethod.POST)
    public RestResultDto addRedisString(HttpServletRequest request,String key , String value) {
        RestResultDto result = new RestResultDto();
        result.setResult(RestResultDto.RESULT_SUCC);
        try {
            cloudRedisService.setString(key,value);
            return RestResultDto.newSuccess();
        } catch (Exception e) {
            result.setResult(RestResultDto.RESULT_FAIL);
            result.setException(e.getMessage());
            return RestResultDto.newFalid("新增数据到redis", e.getMessage());
        }
    }

}
