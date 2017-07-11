package controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.mvc.With;
import controllers.base.BaseController;
import controllers.base.secure.Secure;
import enums.constants.BizConstants;

public class Admin extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(Admin.class);

    public static void login() {
        renderArgs.put(BizConstants.MSG, flash.get(BizConstants.MSG));
        render();
    }

}