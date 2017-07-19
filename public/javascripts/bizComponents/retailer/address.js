// 覆盖hidden input 不校验
$.validator.setDefaults({
    ignore: ""
});
//供应商的表单验证
function supValidateOpts() {
    // 校验规则
    var options = {
        onkeyup: false,
        rules: {
            'address.name': {
                required: true,
                username: true
            },
            'address.phone': {
                required: true,
                mobile: true
            },
            'address.country': {
                required: true
            },
            'address.province': {
                required: true
            },
            'address.address': {
                required: true
            }
        },
        success: function(label, element) {}
    };
    return options;
}

function supValidateOpts1() {
    // 校验规则
    var options = {
        onkeyup: false,
        rules: {
            'retailerAddress.name': {
                required: true,
                username: true
            },
            'retailerAddress.phone': {
                required: true,
                mobile: true
            },
            'retailerAddress.country': {
                required: true
            },
            'retailerAddress.province': {
                required: true
            },
            'retailerAddress.address': {
                required: true
            }
        },
        success: function(label, element) {}
    };
    return options;
}
$(function() {
    // CDT.addressList = $('#addressList').remove().val();
    // Mustache.parse(CDT.addressList);
    loadCountryHTML();
    loadProviceHTML();
    /*loadCountryHTML1();
    loadProviceHTML1();*/
    //加载地址
    //loadAddressList(false);
    //设置为默认地址
    $(".new-option-r").click(function() {
        $(this).parent('.user-addresslist').addClass("defaultAddr").siblings().removeClass("defaultAddr");
        var aid = $(this).parent().attr('id');
        var param = {
            'id': aid
        }
        Tr.post('/retailer/address/updateDefaultAddress', param, function(data) {
            if (data.code != 200) {
                alert(data.msg);
                return;
            }
            alert('保存成功');
            window.location.reload();
        });
    });
    // 地址信息保存
    $(document).on('click', '#btnSubmitSup .btnSave', function() {
        var validator = $('#frmEditSup').validate(supValidateOpts1());
        if (validator.form()) {
            var param = {
                'address.name': $('#doc-modal-1 #user-name').val(),
                'address.country': $('#doc-modal-1 #selCountry option').val(),
                'address.countryId': $('#doc-modal-1 #selCountry_id').val(),
                'address.province': $('#doc-modal-1 #selProvince').val(),
                'address.provinceId': $('#doc-modal-1 #selProvince_id').val(),
                'address.city': $('#doc-modal-1 #selCity').val(),
                'address.region': $('#doc-modal-1 #selRegion').val(),
                'address.address': $('#doc-modal-1 #txtAddress').val(),
                'address.phone': $('#doc-modal-1 input[name="address.phone"]').val()
            };
            Tr.post('/retailer/address/save', param, function(data) {
                if (data.code != 200) {
                    alert(data.msg);
                    return;
                }
                alert('保存成功');
                window.location.reload();
            });
        }
    });
    // 地址更新
    $(document).on('click', '#btnSubmitSup1 .btnSave', function() {
        var validator = $('#frmEditSup1').validate(supValidateOpts());
        if (validator.form()) {
            var param = {
                'retailerAddress.id': $('#doc-modal-2 #address_id').val(),
                'retailerAddress.name': $('#doc-modal-2 #user-name1').val(),
                'retailerAddress.country': $('#doc-modal-2 #selCountry1 option').val(),
                'retailerAddress.countryId': $('#doc-modal-2 #selCountry_id1').val(),
                'retailerAddress.province': $('#doc-modal-2 #selProvince1').val(),
                'retailerAddress.provinceId': $('#doc-modal-2 #selProvince_id1').val(),
                'retailerAddress.city': $('#doc-modal-2 #selCity1').val(),
                'retailerAddress.region': $('#doc-modal-2 #selRegion1').val(),
                'retailerAddress.address': $('#doc-modal-2 #txtAddress1').val(),
                'retailerAddress.phone': $('#doc-modal-2 #user-phone1').val()
            };
            Tr.post('/retailer/address/update', param, function(data) {
                if (data.code != 200) {
                    alert(data.msg);
                    return;
                }
                alert('保存成功');
                window.location.reload();
            });
        }
    });
    //地址删除
    $(document).on('click', '#addressList .delAddress', function(e) {
        var eve = e ? e : (window.event ? window.event : null);
        var target = eve.target;
        var id = target.id.split('_')[1];
        var param = {
            id: id

        };
        Tr.post('/retailer/address/delete', param, function(data) {
            if (data.code != 200) {
                alert(data.msg);
                return;
            }
            
            window.location.reload();
        });
    });

    // 编辑地址
    $(document).on('click', '#addressList .editAddress', function(e) {
        var eve = e ? e : (window.event ? window.event : null);
        var target = eve.target;
        var id = target.id.split('_')[1];

        $.ajax({
            url: "/retailer/address/get",
            data: {
                id: id
            },
            success: function(result) {
                var obj = result.results;
                $('#doc-modal-2 #user-name1').val(obj.name);
                $('#doc-modal-2 #user-phone1').val(obj.phone);
                $('#doc-modal-2 #address_id').val(obj.id);
            },
            error: function(ex) {
                console.log('can not get the address');
            }
        });

    });

    // 选择国家
    $(document).on('change', '#selCountry', function() {
        var $option = $(this).find('option:selected');
        var id = $option.attr('value');
        if (!id) {
            $(this).prev().val('');
            return;
        } else {
            $('#selCountry option').attr('value', $option.text());
            $(this).siblings('#selCountry_id').val(id);
            $('div[for="selCountry_id"]').html('');
        }
        // 选国外，不显示省市区内容
        if (id != 1) {
            $('#pro_city_region').hide();
        } else {
            $('#pro_city_region').show();
        }
    });
    // 选择国家1
    $(document).on('change', '#selCountry1', function() {
        var $option = $(this).find('option:selected');
        var id = $option.attr('value');
        if (!id) {
            $(this).prev().val('');
            return;
        } else {
            $('#selCountry1 option').attr('value', $option.text());
            $(this).siblings('#selCountry_id1').val(id);
            $('div[for="selCountry_id1"]').html('');
        }
        // 选国外，不显示省市区内容
        if (id != 1) {
            $('#pro_city_region1').hide();
        } else {
            $('#pro_city_region1').show();
        }
    });

    // 选择省份
    $(document).on('change', '#selProvince_id', function() {
        var id = $(this).find('option:selected').attr('rid');
        if (!id) {
            $(this).prev().val('');
            return;
        } else {
            $(this).prev().val($(this).find('option:selected').text());
            $('div[for="selProvince"]').html('');

        }
        // 省份赋值

        var $me = $(this),
            $city = $me.siblings('.city').eq(0),
            $region = $me.siblings('.region').eq(0);
        $city.find('option').slice(1).remove();
        $region.find('option').slice(1).remove();
        if (!$me.find('option:selected').val()) {
            return;
        }
        Tr.get('/dpl/region', {
            id: id
        }, function(data) {
            if (data.code != 200) return;
            if (data.results && data.results.length > 0) {
                $.each(data.results, function(index, obj) {
                    $city.append('<option rid="' + obj.id + '">' + obj.name + '</option>');
                });
                $city.children().eq(1).attr('selected', true);
                //$city.selectedindex= 1;
                $city.change();
            }
        }, {
            loadingMask: false
        });
    });


    // 选择省份
    $(document).on('change', '#selProvince_id1', function() {
        var id = $(this).find('option:selected').attr('rid');
        if (!id) {
            $(this).prev().val('');
            return;
        } else {
            $(this).prev().val($(this).find('option:selected').text());
            $('div[for="selProvince1"]').html('');

        }
        // 省份赋值

        var $me = $(this),
            $city = $me.siblings('.city').eq(0),
            $region = $me.siblings('.region').eq(0);
        $city.find('option').slice(1).remove();
        $region.find('option').slice(1).remove();
        if (!$me.find('option:selected').val()) {
            return;
        }
        Tr.get('/dpl/region', {
            id: id
        }, function(data) {
            if (data.code != 200) return;
            if (data.results && data.results.length > 0) {
                $.each(data.results, function(index, obj) {
                    $city.append('<option rid="' + obj.id + '">' + obj.name + '</option>');
                });
                $city.children().eq(1).attr('selected', true);
                //$city.selectedindex= 1;
                $city.change();
            }
        }, {
            loadingMask: false
        });
    });
    // 选择城市
    $(document).on('change', '.city', function() {
        var id = $(this).find('option:selected').attr('rid');
        if (!id) {
            return;
        }
        $(this).siblings('.itemCity').eq(0).val($(this).find('option:selected').text());
        var $me = $(this),
            $region = $me.siblings('.region').eq(0);
        var id = $me.find('option:selected').attr('rid');
        $region.find('option').slice(1).remove();
        if (!$me.find('option:selected').val()) {
            return;
        }
        Tr.get('/dpl/region', {
            id: id
        }, function(data) {
            if (data.code != 200) return;
            if (data.results && data.results.length > 0) {
                $.each(data.results, function(index, obj) {
                    $region.append('<option rid="' + obj.id + '">' + obj.name + '</option>');
                });
                $region.children().eq(1).attr('selected', true);
                $region.change();
            }
        }, {
            loadingMask: false
        });
    });
});

function loadProviceHTML() {
    var $provice = $('.province');
    Tr.get('/dpl/region', {
        id: 1
    }, function(data) {
        if (data.code != 200) return;
        if (data.results && data.results.length > 0) {
            $.each(data.results, function(index, obj) {
                $provice.append('<option  value="' + obj.id + '" rid="' + obj.id + '">' + obj.name + '</option>');
            });
            var loadV = $provice.data('val');
            if (loadV) {
                $.each($provice.children(), function(index, obj) {
                    if ($(obj).val() == loadV) {
                        $(obj).attr('selected', true);
                    }
                });
            } else {
                $provice.children().eq(0).attr('selected', true);
            }

        }
    }, {
        loadingMask: false
    });
}

function loadCountryHTML() {
    var $country = $('.country');
    Tr.get('/dpl/region', {
        id: 0
    }, function(data) {
        if (data.code != 200) return;
        if (data.results && data.results.length > 0) {
            $.each(data.results, function(index, obj) {
                $country.append('<option  value="' + obj.id + '" rid="' + obj.id + '">' + obj.name + '</option>');
            });
            var loadV = $country.data('val');
            if (loadV) {
                $.each($country.children(), function(index, obj) {
                    if ($(obj).val() == loadV) {
                        $(obj).attr('selected', true);
                    }
                });
            } else {
                $country.children().eq(0).attr('selected', true);
            }

        }
    }, {
        loadingMask: false
    });
}
