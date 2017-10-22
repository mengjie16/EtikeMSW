CDT = {
        orderTemp: null,
        loadedListCache: {},
        currPageNo: 1,
        pageSize: 10,
    }
    //初始化操作
function initBase() {
    $(document).on('click', '#CreateBtn', function() {
        window.location.href = '/retailer/order/edit';
    });
    var currentTime = new Date().Format("yyyy-MM-dd");
    $('.inputDate').datePicker({
        startDate: '2016-01-01',
        endDate: currentTime,
        clickInput: true,
        verticalOffset: 30,
        horizontalOffset: -26
    });
    //查询
    $(document).on('click', '#searchBtn', function() {
        loadList(CDT.currPageNo);
    });
    //取消订单
    $(document).on('click', '.cancelbtn', function() {
        var $me = $(this);
        if (!confirm('确定取消该订单吗？')) {
            return;
        }
        var tradeId = $me.parents('.orderItem').find('input[name=tradeId]').val();
        Tr.post('/retailer/trade/cancel', {
            "tradeId": tradeId,
            'authenticityToken': $('input[name=authenticityToken]').val()
        }, function(data) {
            if (data.code != 200) {
                alert('取消失败');
                return;
            }
            loadList(CDT.currPageNo);
        });
    });
    //比较查询开始时间与结束时间大小
    $(document).on('change', '.inputDate', function() {
        var startTime = $("#startTime").val();
        var start = new Date(startTime.replace("-", "/").replace("-", "/"));
        var endTime = $("#endTime").val();
        var end = new Date(endTime.replace("-", "/").replace("-", "/"));
        if (end < start) {
            $(this).val('');
            $('.errorDate').text('结束时间不得小于开始时间');
        } else {
            $('.errorDate').text('');
            return true;
        }
    });
    //付款
    $(document).on('click', '.payFor', function() {
        $(this).next().show();
    });
    $('body').on('click', function(e) {
        if (!$(e.srcElement).is('.payblock *') || !$(e.target).is('.payblock *') || !e.srcElement) {
            $('.payblock').hide();
        }
    });
    // 提交支付
    $(document).on('click', '.paytext', function() {
        $('#payForm').submit();
    });


}


//加载订单列表
function loadList(pageNo) {
    if (!pageNo) {
        pageNo = 1;
    }
    var obj = {
        'vo.id': $('#orderNo').val(),
        'vo.pageNo': pageNo,
        'vo.pageSize': CDT.pageSize,
        'vo.createTimeStart': $('#startTime').val(),
        'vo.createTimeEnd': $('#endTime').val()
    };
    Tr.get('/retailer/trade/query', obj, function(data) {
        if (data.code != 200 || !data.results) return;
        // 保存到缓存
        CDT.loadedListCache = data.results;
        var output = Mustache.render(CDT.orderTemp, $.extend(data, {
            'paymentStr': function() {
                if (this.cargoFee) {
                    return this.cargoFee / 100;
                }
                return 0;
            },
            'shippingFeeStr': function() {
                if (this.shippingFee) {
                    return this.shippingFee / 100;
                }
                return 0;
            },
            'totalAmountStr': function() {
                if (this.totalFee) {
                    return this.totalFee / 100;
                }
                return 0;
            },
            'realityPaymentStr': function() {
                if (this.payment) {
                    return this.payment / 100;
                }
                return 0;
            },
            'discountStr': function() {
                if (this.discountFee) {
                    return this.discountFee / 100;
                }
                return 0;
            },
            orderOperation: function() {
                if (this.status == "TRADE_UNPAIED") {//待付款
                    return 'cancelOrder';
                } else if (this.status == "TRADE_USER_CANCELLED" || this.status == "TRADE_CLOSE") {//交易取消，交易成功
                    return 'deleteOrder';
                } else if (this.status == "TRADE_UNRECIIVED") {//待收货
                    return 'Receiving';
                } else {
                    return 'hideOperation';
                }
            },
            orderOperationStr: function() {
                if (this.status == "TRADE_UNPAIED") {
                    return '取消订单'
                } else if (this.status == "TRADE_USER_CANCELLED" ||  this.status == "TRADE_CLOSE") {
                    return '删除订单'
                } else if (this.status == "TRADE_UNRECIIVED") {
                    return '确认收货';
                } else {
                    return ''
                }
            },
            moneyBack: function() {
                if (this.status == "TRADE_UNSEND" ||this.status == "TRADE_UNRECIIVED") {//待发货 待收货
                    return 'drawback';
                }else {
                    return 'hideDrawback';
                }
            },
            moneyBackStr: function() {
                if (this.status == "TRADE_UNSEND" ||this.status == "TRADE_UNRECIIVED") {//待发货 待收货
                    return '退款';
                }else {
                    return '';
                }
            },
            expressage: function() {
                if (this.status == "TRADE_UNRECIIVED") {//待收货
                    return 'expressage';
                }else {
                    return 'hideExpressage';
                }
            },
            expressageStr: function() {
                if (this.status == "TRADE_UNRECIIVED") {//待收货
                    return '查看物流';
                }else {
                    return '';
                }
            },
            payToAli: function() {
                if (this.status == "TRADE_UNPAIED") {//待收货
                    return 'paytoali';
                }else {
                    return 'hidepaytoali';
                }
            },
            payToAliStr:function(){
                if (this.status == "TRADE_UNPAIED") {//待收货
                    return '付款';
                }else {
                    return '';
                }
            }
        }));
        $('#listContainer1').html(output);
        if (data.totalCount <= 0) {
            $('.pagination').hide();
            return;
        }
        $('.pagination').show();
        $('.pagination').pagination(data.totalCount, {
            items_per_page: CDT.pageSize,
            num_display_entries: 5,
            current_page: pageNo,
            num_edge_entries: 5,
            callback: loadListCallBack,
            callback_run: false,
            prev_text: ' '
        });
        CDT.currPageNo = pageNo;
    }, {
        loadingMask: true
    });
}

function loadListCallBack(index, jq) {
    loadList(index + 1);
}

function initRestList(e) {
    var eve = e ? e : (window.event ? window.event : null);
    var target = eve.target;
    var id = target.id;
    var tradeStatus;
    var tabindex;
    switch (id) {
        case 'toPay':
            tradeStatus = 'TRADE_UNPAIED';
            tabindex = 2;
            break;
        case 'toSendGoods':
            tradeStatus = 'TRADE_UNSEND';
            tabindex = 3;
            break;
        case 'toGetGodds':
            tradeStatus = 'TRADE_UNRECIIVED';
            tabindex = 4;
            break;
    }
    renderRestList(1, tradeStatus, tabindex);
}

function renderRestList(pageNo, tradeStatus, tabindex) {

        var obj = {
            'vo.id': $('#orderNo').val(),
            'vo.pageNo': pageNo,
            'vo.pageSize': CDT.pageSize,
            'vo.createTimeStart': $('#startTime').val(),
            'vo.createTimeEnd': $('#endTime').val(),
            'status': tradeStatus
        };
        Tr.get('/retailer/trade/queryByTradeStatus', obj, function(data) {
            /*      if (data.code != 200||!data.results) return;
                    // 保存到缓存
                    CDT.loadedListCache = data.results;*/
            var output = Mustache.render(CDT.orderTemp, $.extend(data, {
                'paymentStr': function() {
                    if (this.cargoFee) {
                        return this.cargoFee / 100;
                    }
                    return 0;
                },
                'shippingFeeStr': function() {
                    if (this.shippingFee) {
                        return this.shippingFee / 100;
                    }
                    return 0;
                },
                'totalAmountStr': function() {
                    if (this.totalFee) {
                        return this.totalFee / 100;
                    }
                    return 0;
                },
                'realityPaymentStr': function() {
                    if (this.payment) {
                        return this.payment / 100;
                    }
                    return 0;
                },
                'discountStr': function() {
                    if (this.discountFee) {
                        return this.discountFee / 100;
                    }
                    return 0;
                },
                orderOperation: function() {
                    if (this.status == "TRADE_UNPAIED") {//待付款
                        return 'cancelOrder';
                    } else if (this.status == "TRADE_USER_CANCELLED" || this.status == "TRADE_CLOSE") {//交易取消，交易成功
                        return 'deleteOrder';
                    } else if (this.status == "TRADE_UNRECIIVED") {//待收货
                        return 'Receiving';
                    } else {
                        return 'hideOperation';
                    }
                },
                orderOperationStr: function() {
                    if (this.status == "TRADE_UNPAIED") {
                        return '取消订单'
                    } else if (this.status == "TRADE_USER_CANCELLED" ||  this.status == "TRADE_CLOSE") {
                        return '删除订单'
                    } else if (this.status == "TRADE_UNRECIIVED") {
                        return '确认收货';
                    } else {
                        return ''
                    }
                },
                moneyBack: function() {
                    if (this.status == "TRADE_UNSEND" ||this.status == "TRADE_UNRECIIVED") {//待发货 待收货
                        return 'drawback';
                    }else {
                        return 'hideDrawback';
                    }
                },
                moneyBackStr: function() {
                    if (this.status == "TRADE_UNSEND" ||this.status == "TRADE_UNRECIIVED") {//待发货 待收货
                        return '退款';
                    }else {
                        return '';
                    }
                },
                expressage: function() {
                    if (this.status == "TRADE_UNRECIIVED") {//待收货
                        return 'expressage';
                    }else {
                        return 'hideExpressage';
                    }
                },
                expressageStr: function() {
                    if (this.status == "TRADE_UNRECIIVED") {//待收货
                        return '查看物流';
                    }else {
                        return '';
                    }
                },
                payToAli: function() {
                    if (this.status == "TRADE_UNPAIED") {//待收货
                        return 'paytoali';
                    }else {
                        return 'hidepaytoali';
                    }
                },
                payToAliStr:function(){
                    if (this.status == "TRADE_UNPAIED") {//待收货
                        return '付款';
                    }else {
                        return '';
                    }
                }
            }));
            $('#listContainer' + tabindex).html(output);
            if (data.totalCount <= 0) {
                $('.pagination').hide();
                return;
            }
            $('.pagination').show();
            $('.pagination').pagination(data.totalCount, {
                items_per_page: CDT.pageSize,
                num_display_entries: 5,
                current_page: pageNo,
                num_edge_entries: 5,
                callback: loadListCallBack,
                callback_run: false,
                prev_text: ' '
            });
            CDT.currPageNo = pageNo;
        }, {
            loadingMask: true
        });
    }
    //取消订单
function cancelOrder() {
        var tradeId = $(this).data('id');
        var param = {
            'tradeId': tradeId
        };
        Tr.post('/retailer/trade/cancel', param, function(data) {
            if (data.code != 200) {
                alert('取消订单失败');
                return;
            } else {

                alert('取消订单成功');
                window.location.reload();
            }

        });

    }
    //删除订单
function deleteOrder() {
        var that = this;
        var tradeId = $(this).data('id');
        var param = {
            'tradeId': tradeId
        };
        Tr.post('/retailer/order/delete', param, function(data) {
            if (data.code != 200) {
                alert('删除订单失败');
                return;
            } else {

                //alert('删除订单成功');
                window.location.reload();
            }

        });
    }
    //确认收货
function Receiving() {
    var tradeId = $(this).data('id');
    var param = {
        "orderVo.tradeId": tradeId,
        "orderVo.statusCode": 'TRADE_CLOSE',
        "orderVo.statusText": '交易关闭'
    };
    Tr.post('/retailer/order/update', param, function(data) {
        if (data.code != 200) {
            alert('确认收货失败');
            return;
        } else {


            window.location.reload();
        }

    });
}
$(function() {
    CDT.orderTemp = $('#orderTemp').remove().val();
    Mustache.parse(CDT.orderTemp);
    //initBase();
    loadList(1);
    $(document).on('click', '#toPay,#toSendGoods,#toGetGodds', initRestList);
    $(document).on('click', '#allOrders', loadList);
    $(document).on('click', '.cancelOrder', cancelOrder);
    $(document).on('click', '.deleteOrder', deleteOrder);
    $(document).on('click', '.Receiving', Receiving);
});
