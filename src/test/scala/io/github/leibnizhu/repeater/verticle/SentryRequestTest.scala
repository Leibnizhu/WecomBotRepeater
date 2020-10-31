package io.github.leibnizhu.repeater.verticle

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.{DefaultScalaModule, ScalaObjectMapper}
import io.github.leibnizhu.repeater.wecom.message.MessageType
import org.scalatest.FunSuite
import org.slf4j.LoggerFactory

/**
 * @author Leibniz on 2020/10/30 6:25 PM
 */
class SentryRequestTest extends FunSuite {
  private val log = LoggerFactory.getLogger(getClass)
  private val objectReader = (new ObjectMapper() with ScalaObjectMapper).registerModule(DefaultScalaModule).readerFor(classOf[SentryRequest])
  private val sentryRequestJson = "{\n    \"project_name\": \"Vue\",\n    \"message\": \"This is an example Python exception\",\n    \"id\": \"663\",\n    \"culprit\": \"raven.scripts.runner in main\",\n    \"project_slug\": \"project-test\",\n    \"url\": \"http://baidu.com/sentry/project-test/issues/9527/?referrer=webhooks_plugin\",\n    \"level\": \"error\",\n    \"triggering_rules\": [],\n    \"event\": {\n        \"stacktrace\": {\n            \"frames\": [\n                {\n                    \"function\": \"build_msg\",\n                    \"abs_path\": \"/home/ubuntu/base.py\",\n                    \"pre_context\": [\n                        \"                frames = stack\",\n                        \"\",\n                        \"            data.update({\",\n                        \"                'sentry.interfaces.Stacktrace': {\",\n                        \"                    'frames': get_stack_info(frames,\"\n                    ],\n                    \"post_context\": [\n                        \"                },\",\n                        \"            })\",\n                        \"\",\n                        \"        if 'sentry.interfaces.Stacktrace' in data:\",\n                        \"            if self.include_paths:\"\n                    ],\n                    \"vars\": {\n                        \"'frames'\": \"<generator object iter_stack_frames at 0x107bcc3c0>\",\n                        \"'culprit'\": null,\n                        \"'event_type'\": \"'raven.events.Message'\",\n                        \"'date'\": \"datetime.datetime(2013, 8, 13, 3, 8, 24, 880386)\",\n                        \"'extra'\": {\n                            \"'go_deeper'\": [\n                                [\n                                    \"{\\\"'bar'\\\":[\\\"'baz'\\\"],\\\"'foo'\\\":\\\"'bar'\\\"}\"\n                                ]\n                            ],\n                            \"'user'\": \"'dcramer'\",\n                            \"'loadavg'\": [\n                                0.37255859375,\n                                0.5341796875,\n                                0.62939453125\n                            ]\n                        },\n                        \"'v'\": {\n                            \"'message'\": \"u'This is a test message generated using ``raven test``'\",\n                            \"'params'\": []\n                        },\n                        \"'kwargs'\": {\n                            \"'message'\": \"'This is a test message generated using ``raven test``'\",\n                            \"'level'\": 20\n                        },\n                        \"'event_id'\": \"'54a322436e1b47b88e239b78998ae742'\",\n                        \"'tags'\": null,\n                        \"'data'\": {\n                            \"'sentry.interfaces.Message'\": {\n                                \"'message'\": \"u'This is a test message generated using ``raven test``'\",\n                                \"'params'\": []\n                            },\n                            \"'message'\": \"u'This is a test message generated using ``raven test``'\"\n                        },\n                        \"'self'\": \"<raven.base.Client object at 0x107bb8210>\",\n                        \"'time_spent'\": null,\n                        \"'result'\": {\n                            \"'sentry.interfaces.Message'\": {\n                                \"'message'\": \"u'This is a test message generated using ``raven test``'\",\n                                \"'params'\": []\n                            },\n                            \"'message'\": \"u'This is a test message generated using ``raven test``'\"\n                        },\n                        \"'stack'\": true,\n                        \"'handler'\": \"<raven.events.Message object at 0x107bd0890>\",\n                        \"'k'\": \"'sentry.interfaces.Message'\",\n                        \"'public_key'\": null\n                    },\n                    \"module\": \"raven.base\",\n                    \"filename\": \"raven/base.py\",\n                    \"lineno\": 303,\n                    \"in_app\": false,\n                    \"data\": {},\n                    \"context_line\": \"                        transformer=self.transform)\"\n                },\n                {\n                    \"function\": \"capture\",\n                    \"abs_path\": \"/home/ubuntu/.virtualenvs/getsentry/src/raven/raven/base.py\",\n                    \"pre_context\": [\n                        \"        if not self.is_enabled():\",\n                        \"            return\",\n                        \"\",\n                        \"        data = self.build_msg(\",\n                        \"            event_type, data, date, time_spent, extra, stack, tags=tags,\"\n                    ],\n                    \"post_context\": [\n                        \"\",\n                        \"        self.send(**data)\",\n                        \"\",\n                        \"        return (data.get('event_id'),)\",\n                        \"\"\n                    ],\n                    \"vars\": {\n                        \"'event_type'\": \"'raven.events.Message'\",\n                        \"'date'\": null,\n                        \"'extra'\": {\n                            \"'go_deeper'\": [\n                                [\n                                    \"{\\\"'bar'\\\":[\\\"'baz'\\\"],\\\"'foo'\\\":\\\"'bar'\\\"}\"\n                                ]\n                            ],\n                            \"'user'\": \"'dcramer'\",\n                            \"'loadavg'\": [\n                                0.37255859375,\n                                0.5341796875,\n                                0.62939453125\n                            ]\n                        },\n                        \"'stack'\": true,\n                        \"'tags'\": null,\n                        \"'data'\": null,\n                        \"'self'\": \"<raven.base.Client object at 0x107bb8210>\",\n                        \"'time_spent'\": null,\n                        \"'kwargs'\": {\n                            \"'message'\": \"'This is a test message generated using ``raven test``'\",\n                            \"'level'\": 20\n                        }\n                    },\n                    \"module\": \"raven.base\",\n                    \"filename\": \"raven/base.py\",\n                    \"lineno\": 459,\n                    \"in_app\": false,\n                    \"data\": {},\n                    \"context_line\": \"            **kwargs)\"\n                },\n                {\n                    \"function\": \"captureMessage\",\n                    \"abs_path\": \"/home/ubuntu/base.py\",\n                    \"pre_context\": [\n                        \"        \\\"\\\"\\\"\",\n                        \"        Creates an event from ``message``.\",\n                        \"\",\n                        \"        >>> client.captureMessage('My event just happened!')\",\n                        \"        \\\"\\\"\\\"\"\n                    ],\n                    \"post_context\": [\n                        \"\",\n                        \"    def captureException(self, exc_info=None, **kwargs):\",\n                        \"        \\\"\\\"\\\"\",\n                        \"        Creates an event from an exception.\",\n                        \"\"\n                    ],\n                    \"vars\": {\n                        \"'message'\": \"'This is a test message generated using ``raven test``'\",\n                        \"'kwargs'\": {\n                            \"'extra'\": {\n                                \"'go_deeper'\": [\n                                    \"[{\\\"'bar'\\\":[\\\"'baz'\\\"],\\\"'foo'\\\":\\\"'bar'\\\"}]\"\n                                ],\n                                \"'user'\": \"'dcramer'\",\n                                \"'loadavg'\": [\n                                    0.37255859375,\n                                    0.5341796875,\n                                    0.62939453125\n                                ]\n                            },\n                            \"'tags'\": null,\n                            \"'data'\": null,\n                            \"'level'\": 20,\n                            \"'stack'\": true\n                        },\n                        \"'self'\": \"<raven.base.Client object at 0x107bb8210>\"\n                    },\n                    \"module\": \"raven.base\",\n                    \"filename\": \"raven/base.py\",\n                    \"lineno\": 577,\n                    \"in_app\": false,\n                    \"data\": {},\n                    \"context_line\": \"        return self.capture('raven.events.Message', message=message, **kwargs)\"\n                },\n                {\n                    \"function\": \"send_test_message\",\n                    \"abs_path\": \"/home/ubuntu/runner.py\",\n                    \"pre_context\": [\n                        \"        level=logging.INFO,\",\n                        \"        stack=True,\",\n                        \"        tags=options.get('tags', {}),\",\n                        \"        extra={\",\n                        \"            'user': get_uid(),\"\n                    ],\n                    \"post_context\": [\n                        \"        },\",\n                        \"    ))\",\n                        \"\",\n                        \"    if client.state.did_fail():\",\n                        \"        print('error!')\"\n                    ],\n                    \"vars\": {\n                        \"'client'\": \"<raven.base.Client object at 0x107bb8210>\",\n                        \"'options'\": {\n                            \"'tags'\": null,\n                            \"'data'\": null\n                        },\n                        \"'data'\": null,\n                        \"'k'\": \"'secret_key'\"\n                    },\n                    \"module\": \"raven.scripts.runner\",\n                    \"filename\": \"raven/scripts/runner.py\",\n                    \"lineno\": 77,\n                    \"in_app\": false,\n                    \"data\": {},\n                    \"context_line\": \"            'loadavg': get_loadavg(),\"\n                },\n                {\n                    \"function\": \"main\",\n                    \"abs_path\": \"/home/ubuntu/runner.py\",\n                    \"pre_context\": [\n                        \"    print(\\\"Using DSN configuration:\\\")\",\n                        \"    print(\\\" \\\", dsn)\",\n                        \"    print()\",\n                        \"\",\n                        \"    client = Client(dsn, include_paths=['raven'])\"\n                    ],\n                    \"vars\": {\n                        \"'root'\": \"<logging.Logger object at 0x107ba5b10>\",\n                        \"'parser'\": \"<optparse.OptionParser instance at 0x107ba3368>\",\n                        \"'dsn'\": \"'https://aaa:bbb@sentry.io/1'\",\n                        \"'opts'\": \"<Values at 0x107ba3b00: {'data': None, 'tags': None}>\",\n                        \"'client'\": \"<raven.base.Client object at 0x107bb8210>\",\n                        \"'args'\": [\n                            \"'test'\",\n                            \"'https://aaa:bbb@sentry.io/1'\"\n                        ]\n                    },\n                    \"module\": \"raven.scripts.runner\",\n                    \"filename\": \"raven/scripts/runner.py\",\n                    \"lineno\": 112,\n                    \"in_app\": false,\n                    \"data\": {},\n                    \"context_line\": \"    send_test_message(client, opts.__dict__)\"\n                }\n            ]\n        },\n        \"use_rust_normalize\": true,\n        \"extra\": {\n            \"emptyList\": [],\n            \"unauthorized\": false,\n            \"emptyMap\": {},\n            \"url\": \"http://example.org/foo/bar/\",\n            \"results\": [\n                1,\n                2,\n                3,\n                4,\n                5\n            ],\n            \"length\": 10837790,\n            \"session\": {\n                \"foo\": \"bar\"\n            }\n        },\n        \"modules\": {\n            \"my.package\": \"1.0.0\"\n        },\n        \"_ref_version\": 2,\n        \"_ref\": 7,\n        \"culprit\": \"raven.scripts.runner in main\",\n        \"title\": \"This is an example Python exception\",\n        \"event_id\": \"9b762154cb354649bf06ff67a665d73f\",\n        \"platform\": \"python\",\n        \"version\": \"5\",\n        \"location\": null,\n        \"template\": {\n            \"abs_path\": \"/srv/example/templates/debug_toolbar/base.html\",\n            \"pre_context\": [\n                \"{% endif %}\\n\",\n                \"<script src=\\\"{% static 'debug_toolbar/js/toolbar.js' %}\\\"></script>\\n\",\n                \"<div id=\\\"djDebug\\\" hidden=\\\"hidden\\\" dir=\\\"ltr\\\"\\n\"\n            ],\n            \"post_context\": [\n                \"     {{ toolbar.config.ROOT_TAG_EXTRA_ATTRS|safe }}>\\n\",\n                \"\\t<div hidden=\\\"hidden\\\" id=\\\"djDebugToolbar\\\">\\n\",\n                \"\\t\\t<ul id=\\\"djDebugPanelList\\\">\\n\"\n            ],\n            \"filename\": \"debug_toolbar/base.html\",\n            \"lineno\": 14,\n            \"context_line\": \"     data-store-id=\\\"{{ toolbar.store_id }}\\\" data-render-panel-url=\\\"{% url 'djdt:render_panel' %}\\\"\\n\"\n        },\n        \"logger\": \"\",\n        \"type\": \"default\",\n        \"metadata\": {\n            \"title\": \"This is an example Python exception\"\n        },\n        \"tags\": [\n            [\n                \"browser\",\n                \"Chrome 28.0.1500\"\n            ],\n            [\n                \"browser.name\",\n                \"Chrome\"\n            ],\n            [\n                \"level\",\n                \"error\"\n            ],\n            [\n                \"os.name\",\n                \"Windows 8\"\n            ],\n            [\n                \"sentry:user\",\n                \"id:1\"\n            ],\n            [\n                \"url\",\n                \"http://example.com/foo\"\n            ]\n        ],\n        \"timestamp\": 1604038295.742,\n        \"user\": {\n            \"username\": \"sentry\",\n            \"name\": \"Sentry\",\n            \"ip_address\": \"127.0.0.1\",\n            \"email\": \"sentry@example.com\",\n            \"geo\": {\n                \"city\": \"London\",\n                \"region\": \"H9\",\n                \"country_code\": \"GB\"\n            },\n            \"id\": \"1\"\n        },\n        \"fingerprint\": [\n            \"{{ default }}\"\n        ],\n        \"hashes\": [\n            \"c4a4d06bc314205bb3b6bdb612dde7f1\"\n        ],\n        \"received\": 1604038295.742,\n        \"level\": \"error\",\n        \"contexts\": {\n            \"os\": {\n                \"version\": null,\n                \"name\": \"Windows 8\"\n            },\n            \"browser\": {\n                \"version\": \"28.0.1500\",\n                \"name\": \"Chrome\"\n            }\n        },\n        \"request\": {\n            \"cookies\": [\n                [\n                    \"foo\",\n                    \"bar\"\n                ],\n                [\n                    \"biz\",\n                    \"baz\"\n                ]\n            ],\n            \"url\": \"http://example.com/foo\",\n            \"headers\": [\n                [\n                    \"Content-Type\",\n                    \"application/json\"\n                ],\n                [\n                    \"Referer\",\n                    \"http://example.com\"\n                ],\n                [\n                    \"User-Agent\",\n                    \"Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.72 Safari/537.36\"\n                ]\n            ],\n            \"env\": {\n                \"ENV\": \"prod\"\n            },\n            \"query_string\": [\n                [\n                    \"foo\",\n                    \"bar\"\n                ]\n            ],\n            \"data\": {\n                \"hello\": \"world\"\n            },\n            \"method\": \"GET\",\n            \"inferred_content_type\": \"application/json\"\n        },\n        \"logentry\": {\n            \"formatted\": \"This is an example Python exception\"\n        }\n    },\n    \"project\": \"test-project\",\n    \"logger\": null\n}"

  test("textMessageTest") {
    val sentryRequest = objectReader.readValue[SentryRequest](sentryRequestJson)
    val reqJson = sentryRequest.toWecomBotRequest("12345678980", MessageType.Text, List()).msgContent.wholeJson()
    log.info("requset json:{}", reqJson)
    assert(reqJson.getString("msgtype") == "text")
    assert(reqJson.getJsonObject("text") != null)
    assert(reqJson.getJsonObject("text").getString("content") != null)
  }

  test("textMessageWithMentionTest") {
    val sentryRequest = objectReader.readValue[SentryRequest](sentryRequestJson)
    val reqJson = sentryRequest.toWecomBotRequest("12345678980", MessageType.Text, List("18888888888")).msgContent.wholeJson()
    log.info("requset json:{}", reqJson)
    assert(reqJson.getString("msgtype") == "text")
    assert(reqJson.getJsonObject("text") != null)
    assert(reqJson.getJsonObject("text").getString("content") != null)
    assert(reqJson.getJsonObject("text").getJsonArray("mentioned_mobile_list") != null)
  }

  test("markdownMessageTest") {
    val sentryRequest = objectReader.readValue[SentryRequest](sentryRequestJson)
    val reqJson = sentryRequest.toWecomBotRequest("12345678980", MessageType.Markdown, List()).msgContent.wholeJson()
    log.info("requset json:{}", reqJson)
    assert(reqJson.getString("msgtype") == "markdown")
    assert(reqJson.getJsonObject("markdown") != null)
    assert(reqJson.getJsonObject("markdown").getString("content") != null)
  }

  test("markdownMessageWithMentionTest") {
    val sentryRequest = objectReader.readValue[SentryRequest](sentryRequestJson)
    val reqJson = sentryRequest.toWecomBotRequest("12345678980", MessageType.Markdown, List("test@google.com")).msgContent.wholeJson()
    log.info("requset json:{}", reqJson)
    assert(reqJson.getString("msgtype") == "markdown")
    assert(reqJson.getJsonObject("markdown") != null)
    assert(reqJson.getJsonObject("markdown").getString("content") != null)
  }
}