/*******************************************************************************
 * Copyright (c) 2015, 2016 Automation RockStars Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License v2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Automation RockStars - initial API and implementation
 *******************************************************************************/
package com.automationrockstars.bmo;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.ArrayUtils;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automationrockstars.base.ConfigLoader;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import javassist.Modifier;

@RunReporter
public class CompositeStoryReporter implements StoryReporter {

	private static final List<StoryReporter> reporters = Lists.newArrayList();
	private static CompositeStoryReporter INSTANCE;

	public static CompositeStoryReporter create() {
		INSTANCE = new CompositeStoryReporter();
		return INSTANCE;
	}

	private static final Lock lock = new ReentrantLock();

	public static Collection<StoryReporter> subReporters() {
		return Collections.unmodifiableCollection(reporters);
	}

	public String name() {
		return "Composite";
	}

	private static final Logger LOG = LoggerFactory.getLogger(CompositeStoryReporter.class);

	public static CompositeStoryReporter reporter() {
		if (INSTANCE == null) {
			create();
		}
		return INSTANCE;
	}

	private CompositeStoryReporter() {
	}

	@Override
	public void start() {
		try {
			lock.lock();
			for (StoryReporter reporter : reporters) {
				try {
					reporter.start();
				} catch (Throwable failure) {
					LOG.error("Reporter {} failed to start", reporter.name(), failure);
				}
			}
		} finally {
			lock.unlock();
		}

	}

	@Override
	public void finish() {
		try {
			lock.lock();
			for (StoryReporter reporter : reporters) {
				try {
					reporter.finish();
				} catch (Throwable failure) {
					LOG.error("Reporter {} failed to finish", reporter.name(), failure);
				}
			}
		} finally {
			lock.unlock();
		}

	}

	@Override
	public void beforeStory(String name, String description, String path) {
		try {
			lock.lock();
			for (StoryReporter reporter : reporters) {
				try {
					reporter.beforeStory(name, description, path);
				} catch (Throwable failure) {
					LOG.error("Reporter {} failed to run before story", reporter.name(), failure);
				}
			}
		} finally {
			lock.unlock();
		}

	}

	@Override
	public void afterStory() {
		try {
			lock.lock();
			for (StoryReporter reporter : reporters) {
				try {
					reporter.afterStory();
				} catch (Throwable failure) {
					LOG.error("Reporter {} failed to run afer story", reporter.name(), failure);
				}
			}
		} finally {
			lock.unlock();
		}

	}

	@Override
	public void beforeScenario(String scenarioTitle) {
		try {
			lock.lock();
			for (StoryReporter reporter : reporters) {
				try {
					reporter.beforeScenario(scenarioTitle);
				} catch (Throwable failure) {
					LOG.error("Reporter {} failed to run before scenario", reporter.name(), failure);
				}
			}
		} finally {
			lock.unlock();
		}

	}

	@Override
	public void afterScenario() {
		try {
			lock.lock();
			for (StoryReporter reporter : reporters) {
				try {
					reporter.afterScenario();
				} catch (Throwable failure) {
					LOG.error("Reporter {} failed to run after scenario", reporter.name(), failure);
				}
			}
		} finally {
			lock.unlock();
		}

	}

	@Override
	public void example(Map<String, String> tableRow) {
		try {
			lock.lock();

			for (StoryReporter reporter : reporters) {
				try {
					reporter.example(tableRow);
				} catch (Throwable failure) {
					LOG.error("Reporter {} failed to run example", reporter.name(), failure);
				}
			}
		} finally {
			lock.unlock();
		}

	}

	@Override
	public void beforeStep(String step) {
		try {
			lock.lock();
			for (StoryReporter reporter : reporters) {
				try {
					reporter.beforeStep(step);
				} catch (Throwable failure) {
					LOG.error("Reporter {} failed to run before step", reporter.name(), failure);
				}
			}
		} finally {
			lock.unlock();
		}

	}

	@Override
	public void successful(String step) {
		try {
			lock.lock();
			for (StoryReporter reporter : reporters) {
				try {
					reporter.successful(step);
				} catch (Throwable failure) {
					LOG.error("Reporter {} failed to run successfull", reporter.name(), failure);
				}
			}
		} finally {
			lock.unlock();
		}

	}

	@Override
	public void ignorable(String step) {
		try {
			lock.lock();
			for (StoryReporter reporter : reporters) {
				try {
					reporter.ignorable(step);
				} catch (Throwable failure) {
					LOG.error("Reporter {} failed to run ignorable", reporter.name(), failure);
				}
			}
		} finally {
			lock.unlock();
		}

	}

	@Override
	public void pending(String step) {
		try {
			lock.lock();
			for (StoryReporter reporter : reporters) {
				try {
					reporter.pending(step);
				} catch (Throwable failure) {
					LOG.error("Reporter {} failed to run pending ", reporter.name(), failure);
				}
			}
		} finally {
			lock.unlock();
		}

	}

	@Override
	public void notPerformed(String step) {
		try {
			lock.lock();
			for (StoryReporter reporter : reporters) {
				try {
					reporter.notPerformed(step);
				} catch (Throwable failure) {
					LOG.error("Reporter {} failed to run not performed", reporter.name(), failure);
				}
			}
		} finally {
			lock.unlock();
		}

	}

	@Override
	public void failed(String step, Throwable cause) {
		try {
			lock.lock();
			for (StoryReporter reporter : reporters) {
				try {
					reporter.failed(step, cause);
				} catch (Throwable failure) {
					LOG.error("Reporter {} failed to run failed", reporter.name(), failure);
				}
			}
		} finally {
			lock.unlock();
		}

	}

	@Override
	public void attach(byte[] attachment, String title, String mimeType) {
		try {
			lock.lock();
			for (StoryReporter reporter : reporters) {
				try {
					reporter.attach(attachment, title, mimeType);
				} catch (Throwable failure) {
					LOG.error("Reporter {} failed to run attach", reporter.name(), failure);
				}
			}
		} finally {
			lock.unlock();
		}

	}

	public static void add(final StoryReporter reporter) {
		try {
			lock.lock();
			if (!Iterables.tryFind(reporters, new Predicate<StoryReporter>() {
				@Override
				public boolean apply(StoryReporter input) {
					return input.getClass().equals(reporter.getClass());
				}
			}).isPresent()) {
				reporters.add(reporter);

				Collections.sort(reporters, new Comparator<StoryReporter>() {

					@Override
					public int compare(StoryReporter o1, StoryReporter o2) {
						int o1Order = 1000;
						int o2Order = 1000;
						try {
							o1Order = (int) o1.getClass().getMethod("order", null).invoke(o1, null);
						} catch (Throwable ignore) {
						}
						try {
							o2Order = (int) o2.getClass().getMethod("order", null).invoke(o2, null);
						} catch (Throwable ignore) {
						}
						return o1Order - o2Order;
					}
				});
			}
		} finally {
			lock.unlock();
		}
	}

	public static void remove(StoryReporter reporter) {
		try {
			lock.lock();
			reporters.remove(reporter);
		} finally {
			lock.unlock();
		}

	}

	public static void remove(final String name) {
		Optional<StoryReporter> toRemove = Iterables.tryFind(reporters, new Predicate<StoryReporter>() {

			@Override
			public boolean apply(StoryReporter input) {
				return input.name().equals(name);
			}
		});
		if (toRemove.isPresent()) {
			remove(toRemove.get());
		} else {
			LOG.error("Cannot remove {} as it is not in the reporters", name);
		}
	}

	public static void load() {
		create();
		String[] reporterPackages = ConfigLoader.config().getStringArray("story.reporter.package");
		if (ArrayUtils.isNotEmpty(reporterPackages)) {
			reporterPackages = ArrayUtils.add(reporterPackages, "com.automationrockstars");
		} else {
			reporterPackages = new String[] { "com.automationrockstars" };
		}
		Reflections r = new Reflections((Object[]) reporterPackages);
		Set<Class<? extends StoryReporter>> reporterClasses = r.getSubTypesOf(StoryReporter.class);
		for (Class<? extends StoryReporter> reporter : reporterClasses) {
			if (!reporter.getName().equals(CompositeStoryReporter.class.getName())) {
				try {
					if (! Modifier.isAbstract(reporter.getModifiers())){
						StoryReporter rep = reporter.newInstance();
						add(rep);
						LOG.info("Reporter {} added", rep.name());
					}
				} catch (Exception e) {
					LOG.error("Reporter {} cannot be loaded", reporter, e);
				}
			}
		}
	}

	public int order() {
		return 0;
	}

}
